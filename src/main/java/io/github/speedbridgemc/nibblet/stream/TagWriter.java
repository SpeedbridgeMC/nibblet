package io.github.speedbridgemc.nibblet.stream;

import io.github.speedbridgemc.nibblet.MalformedTagException;
import io.github.speedbridgemc.nibblet.TagType;
import io.github.speedbridgemc.nibblet.util.ModUTF8Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public final class TagWriter implements Closeable {
    private final @NotNull TagStreamHandler streamHandler;
    private final @NotNull OutputStream out;
    private @NotNull Context ctx;
    private @Nullable String deferredName;

    private enum Mode {
        ROOT_UNDETERMINED(TagType.END, TagType.END),
        ROOT_COMPOUND(TagType.COMPOUND, TagType.END),
        ROOT_LIST(TagType.ROOT_LIST, TagType.END),
        COMPOUND(TagType.COMPOUND, TagType.END),
        LIST(TagType.LIST, TagType.END),
        BYTE_ARRAY(TagType.BYTE_ARRAY, TagType.BYTE),
        INT_ARRAY(TagType.INT_ARRAY, TagType.INT),
        LONG_ARRAY(TagType.LONG_ARRAY, TagType.LONG);

        public final @NotNull TagType type;
        public final @NotNull TagType listType;

        Mode(@NotNull TagType type, @NotNull TagType listType) {
            this.type = type;
            this.listType = listType;
        }
    }

    private final class Context {
        public final @NotNull Mode mode;
        private final @Nullable Context next;
        private final @NotNull ArrayList<@NotNull DeferredWrite> deferredWrites;
        public @NotNull TagType listType;
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
                out.write(TagType.END.id());
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

        @Override
        public String toString() {
            return "Context{" +
                    "mode=" + mode +
                    "}@" + Integer.toHexString(hashCode());
        }
    }

    public TagWriter(@NotNull TagStreamHandler streamHandler, @NotNull OutputStream out) {
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
            throw new MalformedTagException(errMsg);
    }

    public @NotNull TagWriter name(@NotNull String name) throws IOException {
        switch (ctx.mode) {
        case ROOT_UNDETERMINED:
        case ROOT_COMPOUND:
        case COMPOUND:
            deferredName = name;
            break;
        default:
            throw new MalformedTagException("Names are not allowed outside of compound tags");
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
    
    private void value(@NotNull TagType type, @NotNull DeferredWrite write, int size) throws IOException {
        boolean list = false;
        switch (ctx.mode) {
        case ROOT_UNDETERMINED:
            if (deferredName == null)
                throw new MalformedTagException("Missing root tag name");
            if (type == TagType.COMPOUND)
                ctx = new Context(Mode.ROOT_COMPOUND, null);
            else if (type == TagType.LIST) {
                ctx = new Context(Mode.ROOT_LIST, null);
                out.write(type.id());
                string(deferredName);
                deferredName = null;
                list = true;
                break;
            } else
                throw new MalformedTagException(type + " cannot be a root tag");
        case ROOT_COMPOUND:
            if (deferredName == null)
                throw new MalformedTagException("Missing tag name");
            out.write(type.id());
            string(deferredName);
            deferredName = null;
            write.write();
            break;
        case COMPOUND:
            if (deferredName == null)
                throw new MalformedTagException("Missing tag name");
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
                throw new MalformedTagException("Tried to add " + type + " to " + ctx.mode);
            break;
        default:
            throw new InternalError("Unhandled context mode " + ctx.mode);
        }
        if (list) {
            if (ctx.listType == TagType.END)
                ctx.listType = type;
            else if (ctx.listType != type)
                throw new MalformedTagException("Tried to add " + type + " to list of " + ctx.listType);
            ctx.write(write);
            ctx.listSize += size;
        }
    }
    
    private void value(@NotNull TagType type, @NotNull DeferredWrite write) throws IOException {
        value(type, write, 1);
    }

    public @NotNull TagWriter value(byte value) throws IOException {
        value(TagType.BYTE, () -> out.write(value));
        return this;
    }

    public @NotNull TagWriter value(short value) throws IOException {
        value(TagType.SHORT, () -> streamHandler.writeShort(out, value));
        return this;
    }

    public @NotNull TagWriter value(int value) throws IOException {
        value(TagType.INT, () -> streamHandler.writeInt(out, value));
        return this;
    }

    public @NotNull TagWriter value(long value) throws IOException {
        value(TagType.LONG, () -> streamHandler.writeLong(out, value));
        return this;
    }

    public @NotNull TagWriter value(float value) throws IOException {
        value(TagType.FLOAT, () -> streamHandler.writeFloat(out, value));
        return this;
    }

    public @NotNull TagWriter value(double value) throws IOException {
        value(TagType.DOUBLE, () -> streamHandler.writeDouble(out, value));
        return this;
    }

    public @NotNull TagWriter value(@NotNull String value) throws IOException {
        value(TagType.STRING, () -> string(value));
        return this;
    }

    public @NotNull TagWriter values(byte @NotNull ... values) throws IOException {
        final byte[] valuesCopy = values.clone();
        value(TagType.BYTE_ARRAY, () -> {
            for (byte value : valuesCopy)
                out.write(value);
        }, valuesCopy.length);
        return this;
    }

    public @NotNull TagWriter values(int @NotNull ... values) throws IOException {
        final int[] valuesCopy = values.clone();
        value(TagType.INT_ARRAY, () -> {
            for (int value : valuesCopy)
                streamHandler.writeInt(out, value);
        }, valuesCopy.length);
        return this;
    }

    public @NotNull TagWriter values(long @NotNull ... values) throws IOException {
        final long[] valuesCopy = values.clone();
        value(TagType.LONG_ARRAY, () -> {
            for (long value : valuesCopy)
                streamHandler.writeLong(out, value);
        }, valuesCopy.length);
        return this;
    }

    public @NotNull TagWriter beginCompound() throws IOException {
        pushCtx(Mode.COMPOUND);
        return this;
    }

    public @NotNull TagWriter endCompound() throws IOException {
        endCtx(Mode.COMPOUND, "Tried to end compound before starting one");
        return this;
    }

    public @NotNull TagWriter beginList() throws IOException {
        pushCtx(Mode.LIST);
        return this;
    }

    public @NotNull TagWriter endList() throws IOException {
        endCtx(Mode.LIST, "Tried to end list before starting one");
        return this;
    }
    
    public @NotNull TagWriter beginByteArray() throws IOException {
        pushCtx(Mode.BYTE_ARRAY);
        return this;
    }
    
    public @NotNull TagWriter endByteArray() throws IOException {
        endCtx(Mode.BYTE_ARRAY, "Tried to end byte array before starting one");
        return this;
    }

    public @NotNull TagWriter beginIntArray() throws IOException {
        pushCtx(Mode.INT_ARRAY);
        return this;
    }

    public @NotNull TagWriter endIntArray() throws IOException {
        endCtx(Mode.INT_ARRAY, "Tried to end int array before starting one");
        return this;
    }

    public @NotNull TagWriter beginLongArray() throws IOException {
        pushCtx(Mode.LONG_ARRAY);
        return this;
    }

    public @NotNull TagWriter endLongArray() throws IOException {
        endCtx(Mode.LONG_ARRAY, "Tried to end long array before starting one");
        return this;
    }

    @Override
    public void close() throws IOException {
        if (ctx.hasNext())
            throw new MalformedTagException("Unterminated " + ctx.mode);
        ctx.writeDeferred();
        out.close();
    }
}
