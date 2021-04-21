package io.github.speedbridgemc.nibblet.stream;

import io.github.speedbridgemc.nibblet.MalformedNbtException;
import io.github.speedbridgemc.nibblet.NbtType;
import io.github.speedbridgemc.nibblet.util.ModUTF8Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public final class NbtWriter implements Closeable {
    private final @NotNull NbtStreamHandler streamHandler;
    private final @NotNull OutputStream out;
    private @NotNull Context ctx;
    private @Nullable String deferredName;

    private enum Mode {
        ROOT_UNDETERMINED(NbtType.END, NbtType.END),
        ROOT_COMPOUND(NbtType.COMPOUND, NbtType.END),
        ROOT_LIST(NbtType.ROOT_LIST, NbtType.END),
        COMPOUND(NbtType.COMPOUND, NbtType.END),
        LIST(NbtType.LIST, NbtType.END),
        BYTE_ARRAY(NbtType.BYTE_ARRAY, NbtType.BYTE),
        INT_ARRAY(NbtType.INT_ARRAY, NbtType.INT),
        LONG_ARRAY(NbtType.LONG_ARRAY, NbtType.LONG);

        public final @NotNull NbtType type;
        public final @NotNull NbtType listType;

        Mode(@NotNull NbtType type, @NotNull NbtType listType) {
            this.type = type;
            this.listType = listType;
        }
    }

    private final class Context {
        public final @NotNull Mode mode;
        private final @Nullable Context next;
        private final @NotNull ArrayList<@NotNull DeferredWrite> deferredWrites;
        public @NotNull NbtType listType;
        public int listSize;

        public Context(@NotNull Mode mode, @Nullable Context next) {
            this.mode = mode;
            this.next = next;
            deferredWrites = new ArrayList<>();
            listType = mode.listType;
            listSize = 0;
        }

        public void write(@NotNull DeferredWrite write) throws IOException {
            if (mode == Mode.ROOT_COMPOUND)
                write.write();
            else
                deferredWrites.add(write);
        }

        public void writeDeferred() throws IOException {
            switch (mode) {
            case ROOT_LIST:
                out.write(listType.id());
                // root list size is always 1, so it's unspecified
                break;
            case LIST:
                out.write(listType.id());
            default:
                streamHandler.writeInt(out, listSize);
            case ROOT_COMPOUND:
            case COMPOUND:
                break;
            }
            for (DeferredWrite write : deferredWrites)
                write.write();
            if (mode == Mode.COMPOUND)
                out.write(NbtType.END.id());
        }

        public @NotNull Context push(@NotNull Mode newMode) {
            return new Context(newMode, this);
        }

        public boolean hasNext() {
            return next != null;
        }

        public @NotNull Context pop() {
            if (next == null)
                throw new RuntimeException("Popped one too many times!");
            return next;
        }
    }

    public NbtWriter(@NotNull NbtStreamHandler streamHandler, @NotNull OutputStream out) {
        this.streamHandler = streamHandler;
        this.out = out;
        ctx = new Context(Mode.ROOT_UNDETERMINED, null);
    }

    private void pushCtx(@NotNull Mode mode) throws IOException {
        value(mode.type, () -> { });
        ctx = ctx.push(mode);
    }

    private void popCtx() throws IOException {
        if (ctx.hasNext()) {
            final Context thisCtx = ctx;
            ctx = ctx.pop();
            ctx.write(thisCtx::writeDeferred);
        }
    }

    private void endCtx(@NotNull Mode expectedMode, @NotNull String errMsg) throws IOException {
        if (ctx.mode == expectedMode) {
            if (ctx.hasNext())
                popCtx();
            else
                ctx.writeDeferred();
        } else
            throw new MalformedNbtException(errMsg);
    }

    public @NotNull NbtWriter name(@NotNull String name) throws IOException {
        switch (ctx.mode) {
        case ROOT_UNDETERMINED:
        case ROOT_COMPOUND:
        case COMPOUND:
            deferredName = name;
            break;
        default:
            throw new MalformedNbtException("Names are not allowed outside of compound tags");
        }
        return this;
    }

    private void string(@NotNull String value) throws IOException {
        ModUTF8Strings.EncodeResult res = ModUTF8Strings.encode(value);
        streamHandler.writeUTFLength(out, res.utfLength());
        out.write(res.buffer(), 0, res.utfLength());
    }

    @FunctionalInterface
    private interface DeferredWrite {
        void write() throws IOException;
    }
    
    private void value(@NotNull NbtType type, @NotNull DeferredWrite write, int size) throws IOException {
        boolean list = false;
        switch (ctx.mode) {
        case ROOT_UNDETERMINED:
            if (deferredName == null)
                throw new MalformedNbtException("Missing root tag name");
            if (type == NbtType.COMPOUND)
                ctx = new Context(Mode.ROOT_COMPOUND, null);
            else if (type == NbtType.LIST) {
                ctx = new Context(Mode.ROOT_LIST, null);
                out.write(type.id());
                string(deferredName);
                deferredName = null;
                list = true;
                break;
            } else
                throw new MalformedNbtException(type + " cannot be a root tag");
        case ROOT_COMPOUND:
            if (deferredName == null)
                throw new MalformedNbtException("Missing tag name");
            out.write(type.id());
            string(deferredName);
            deferredName = null;
            write.write();
            break;
        case COMPOUND:
            if (deferredName == null)
                throw new MalformedNbtException("Missing tag name");
            final String thisDeferredName = deferredName;
            deferredName = null;
            ctx.write(() -> {
                out.write(type.id());
                string(thisDeferredName);
                write.write();
            });
            break;
        case ROOT_LIST:
        case LIST:
            list = true;
            break;
        case BYTE_ARRAY:
        case INT_ARRAY:
        case LONG_ARRAY:
            if (ctx.mode.type == type || ctx.listType == type) {
                ctx.write(write);
                ctx.listSize += size;
            } else
                throw new MalformedNbtException("Tried to add " + type + " to " + ctx.mode);
            break;
        default:
            throw new InternalError("Unhandled context mode " + ctx.mode);
        }
        if (list) {
            boolean matches = ctx.listType == type;
            if (!matches) {
                switch (ctx.listType) {
                case BYTE:
                    matches = type == NbtType.BYTE_ARRAY;
                    break;
                case INT:
                    matches = type == NbtType.INT_ARRAY;
                    break;
                case LONG:
                    matches = type == NbtType.LONG_ARRAY;
                    break;
                default:
                    break;
                }
            }
            if (ctx.listType == NbtType.END)
                ctx.listType = type;
            else if (!matches)
                throw new MalformedNbtException("Tried to add " + type + " to list of " + ctx.listType);
            ctx.write(write);
            ctx.listSize += size;
        }
    }
    
    private void value(@NotNull NbtType type, @NotNull DeferredWrite write) throws IOException {
        value(type, write, 1);
    }

    public @NotNull NbtWriter value(byte value) throws IOException {
        value(NbtType.BYTE, () -> out.write(value));
        return this;
    }

    public @NotNull NbtWriter value(boolean value) throws IOException {
        return value((byte) (value ? 1 : 0));
    }

    public @NotNull NbtWriter value(short value) throws IOException {
        value(NbtType.SHORT, () -> streamHandler.writeShort(out, value));
        return this;
    }

    public @NotNull NbtWriter value(int value) throws IOException {
        value(NbtType.INT, () -> streamHandler.writeInt(out, value));
        return this;
    }

    public @NotNull NbtWriter value(long value) throws IOException {
        value(NbtType.LONG, () -> streamHandler.writeLong(out, value));
        return this;
    }

    public @NotNull NbtWriter value(float value) throws IOException {
        value(NbtType.FLOAT, () -> streamHandler.writeFloat(out, value));
        return this;
    }

    public @NotNull NbtWriter value(double value) throws IOException {
        value(NbtType.DOUBLE, () -> streamHandler.writeDouble(out, value));
        return this;
    }

    public @NotNull NbtWriter value(@NotNull String value) throws IOException {
        value(NbtType.STRING, () -> string(value));
        return this;
    }

    public @NotNull NbtWriter values(byte @NotNull ... values) throws IOException {
        final byte[] valuesCopy = values.clone();
        value(NbtType.BYTE_ARRAY, () -> {
            for (byte value : valuesCopy)
                out.write(value);
        }, valuesCopy.length);
        return this;
    }

    public @NotNull NbtWriter values(int @NotNull ... values) throws IOException {
        final int[] valuesCopy = values.clone();
        value(NbtType.INT_ARRAY, () -> {
            for (int value : valuesCopy)
                streamHandler.writeInt(out, value);
        }, valuesCopy.length);
        return this;
    }

    public @NotNull NbtWriter values(long @NotNull ... values) throws IOException {
        final long[] valuesCopy = values.clone();
        value(NbtType.LONG_ARRAY, () -> {
            for (long value : valuesCopy)
                streamHandler.writeLong(out, value);
        }, valuesCopy.length);
        return this;
    }

    public @NotNull NbtWriter beginCompound() throws IOException {
        pushCtx(Mode.COMPOUND);
        return this;
    }

    public @NotNull NbtWriter endCompound() throws IOException {
        endCtx(Mode.COMPOUND, "Tried to end compound before starting one");
        return this;
    }

    public @NotNull NbtWriter beginList() throws IOException {
        pushCtx(Mode.LIST);
        return this;
    }

    public @NotNull NbtWriter endList() throws IOException {
        endCtx(Mode.LIST, "Tried to end list before starting one");
        return this;
    }
    
    public @NotNull NbtWriter beginByteArray() throws IOException {
        pushCtx(Mode.BYTE_ARRAY);
        return this;
    }
    
    public @NotNull NbtWriter endByteArray() throws IOException {
        endCtx(Mode.BYTE_ARRAY, "Tried to end byte array before starting one");
        return this;
    }

    public @NotNull NbtWriter beginIntArray() throws IOException {
        pushCtx(Mode.INT_ARRAY);
        return this;
    }

    public @NotNull NbtWriter endIntArray() throws IOException {
        endCtx(Mode.INT_ARRAY, "Tried to end int array before starting one");
        return this;
    }

    public @NotNull NbtWriter beginLongArray() throws IOException {
        pushCtx(Mode.LONG_ARRAY);
        return this;
    }

    public @NotNull NbtWriter endLongArray() throws IOException {
        endCtx(Mode.LONG_ARRAY, "Tried to end long array before starting one");
        return this;
    }

    @Override
    public void close() throws IOException {
        if (ctx.hasNext())
            throw new MalformedNbtException("Unterminated " + ctx.mode);
        ctx.writeDeferred();
        out.close();
    }
}