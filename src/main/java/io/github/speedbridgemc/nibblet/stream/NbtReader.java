package io.github.speedbridgemc.nibblet.stream;

import io.github.speedbridgemc.nibblet.NbtType;
import io.github.speedbridgemc.nibblet.util.MUTF8Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public final class NbtReader implements Closeable {
    private static final ThreadLocal<byte[]> TL_BUFFER = new ThreadLocal<>();

    private static byte @NotNull [] buffer(int length) {
        byte[] buf = TL_BUFFER.get();
        if (buf == null || buf.length < length) {
            buf = new byte[length];
            TL_BUFFER.set(buf);
        }
        return buf;
    }

    private final @NotNull NbtStreamHandler streamHandler;
    private final @NotNull InputStream in;
    private @Nullable NbtType thisType;
    private boolean firstByte;
    private @NotNull Context ctx;
    
    private enum Mode {
        ROOT,
        OBJECT,
        LIST
    }

    private static final class Context {
        public final @NotNull Mode mode;
        private final @Nullable Context next;
        public @NotNull NbtType type;
        public @NotNull NbtType itemType;
        public int size;
        public int itemsRemaining;

        private Context(@NotNull Mode mode, @Nullable Context next) {
            this.mode = mode;
            this.next = next;
            type = NbtType.LIST;
            itemType = NbtType.END;
            size = 0;
            itemsRemaining = 0;
        }
        
        public @NotNull Context push(@NotNull Mode newMode) {
            return new Context(newMode, this);
        }
        
        public @NotNull Context pop() {
            if (next == null)
                throw new RuntimeException("Popped one too many times!");
            return next;
        }
    }

    public NbtReader(@NotNull NbtStreamHandler streamHandler, @NotNull InputStream in) {
        this.streamHandler = streamHandler;
        this.in = in;
        firstByte = true;
        ctx = new Context(Mode.ROOT, null);
    }

    public @NotNull NbtType nextType() throws IOException {
        byte typeId = (byte) (in.read() & 0xFF);
        NbtType type = NbtType.fromId(typeId);
        if (type == null)
            throw new MalformedNbtDataException("Unknown tag type ID " + typeId);
        if (firstByte) {
            firstByte = false;
            if (type == NbtType.LIST)
                type = NbtType.ROOT_LIST;
        }
        thisType = type;
        return type;
    }

    private void expectType(@NotNull NbtType expectedType) throws IOException {
        if (ctx.mode == Mode.LIST) {
            if (ctx.itemType == expectedType) {
                if (--ctx.itemsRemaining < 0)
                    throw new MalformedNbtDataException("List or array is too small");
            } else
                throw new MalformedNbtDataException("Tried to read " + expectedType + " from list or array of " + ctx.itemType);
        } else {
            if (thisType == null)
                nextType();
            if (thisType != expectedType)
                throw new MalformedNbtDataException("Expected " + expectedType + ", got " + thisType);
            thisType = null;
        }
    }

    public void beginObject() throws IOException {
        expectType(NbtType.OBJECT);
        ctx = ctx.push(Mode.OBJECT);
    }

    public void endObject() throws IOException {
        if (ctx.mode != Mode.OBJECT)
            throw new MalformedNbtDataException("Not in an object");
        expectType(NbtType.END);
        ctx = ctx.pop();
    }

    private void beginList0(@NotNull NbtType type, @Nullable NbtType itemType, boolean singleton) throws IOException {
        expectType(type);
        ctx = ctx.push(Mode.LIST);
        ctx.type = type;
        if (itemType == null)
            itemType = nextType();
        ctx.itemType = itemType;
        ctx.size = ctx.itemsRemaining = singleton ? 1 : streamHandler.readInt(in);
    }

    private void endList0(@NotNull NbtType type) throws IOException {
        if (ctx.type != type)
            throw new MalformedNbtDataException("Not in a " + type);
        if (ctx.itemsRemaining > 0)
            throw new MalformedNbtDataException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginList() throws IOException {
        beginList0(NbtType.LIST, null, false);
    }

    public @NotNull NbtType listItemType() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedNbtDataException("Not in a list or array");
        return ctx.itemType;
    }

    public int listSize() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedNbtDataException("Not in a list or array");
        return ctx.size;
    }

    public boolean listHasNext() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedNbtDataException("Not in a list or array");
        return ctx.itemsRemaining > 0;
    }

    public void endList() throws IOException {
        endList0(NbtType.LIST);
    }

    public void beginRootList() throws IOException {
        beginList0(NbtType.ROOT_LIST, null, true);
    }

    public void endRootList() throws IOException {
        endList0(NbtType.ROOT_LIST);
    }

    public void beginByteArray() throws IOException {
        beginList0(NbtType.BYTE_ARRAY, NbtType.BYTE, false);
    }

    public void endByteArray() throws IOException {
        endList0(NbtType.BYTE_ARRAY);
    }

    public void beginIntArray() throws IOException {
        beginList0(NbtType.INT_ARRAY, NbtType.INT, false);
    }

    public void endIntArray() throws IOException {
        endList0(NbtType.INT_ARRAY);
    }

    public void beginLongArray() throws IOException {
        beginList0(NbtType.LONG_ARRAY, NbtType.LONG, false);
    }

    public void endLongArray() throws IOException {
        endList0(NbtType.LONG_ARRAY);
    }

    public @NotNull String nextName() throws IOException {
        firstByte = false;
        int utflen = streamHandler.readUTFLength(in);
        if (utflen == 0)
            return "";
        byte[] buf = buffer(utflen);
        if (in.read(buf, 0, utflen) < utflen)
            throw new IOException("Failed to read entire string");
        return MUTF8Strings.decode(buf, utflen);
    }

    public byte nextByte() throws IOException {
        expectType(NbtType.BYTE);
        return (byte) (in.read() & 0xFF);
    }

    public boolean nextBoolean() throws IOException {
        return nextByte() > 0;
    }

    public short nextShort() throws IOException {
        expectType(NbtType.SHORT);
        return streamHandler.readShort(in);
    }

    public int nextInt() throws IOException {
        expectType(NbtType.INT);
        return streamHandler.readInt(in);
    }

    public long nextLong() throws IOException {
        expectType(NbtType.LONG);
        return streamHandler.readLong(in);
    }

    public float nextFloat() throws IOException {
        expectType(NbtType.FLOAT);
        return streamHandler.readFloat(in);
    }

    public double nextDouble() throws IOException {
        expectType(NbtType.DOUBLE);
        return streamHandler.readDouble(in);
    }

    public @NotNull String nextString() throws IOException {
        expectType(NbtType.STRING);
        return nextName();
    }

    public void skipValue() throws IOException {
        NbtType skippedType;
        if (ctx.mode == Mode.LIST) {
            skippedType = ctx.itemType;
            if (--ctx.itemsRemaining < 0)
                throw new MalformedNbtDataException("List or array is too small");
        } else if (thisType != null) {
            skippedType = thisType;
            thisType = null;
        } else
            skippedType = nextType();
        long bytesToSkip = streamHandler.payloadSize(skippedType);
        if (bytesToSkip >= 0) {
            if (in.skip(bytesToSkip) < bytesToSkip)
                throw new IOException("Failed to skip entire " + skippedType + " value");
            return;
        }
        long payloadSize;
        switch (skippedType) {
        case BYTE:
            //noinspection ResultOfMethodCallIgnored
            in.read();
            break;
        case SHORT:
            streamHandler.skipShort(in);
            break;
        case INT:
            streamHandler.skipInt(in);
            break;
        case LONG:
            streamHandler.skipLong(in);
            break;
        case FLOAT:
            streamHandler.skipFloat(in);
            break;
        case DOUBLE:
            streamHandler.skipDouble(in);
            break;
        case BYTE_ARRAY:
            beginByteArray();
            bytesToSkip = ctx.size;
            if (in.skip(bytesToSkip) < bytesToSkip)
                throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.size + " entries)");
            ctx.size = 0;
            endByteArray();
            break;
        case STRING:
            bytesToSkip = streamHandler.readUTFLength(in);
            if (in.skip(bytesToSkip) < bytesToSkip)
                throw new IOException("Failed to skip entire " + skippedType + " value");
            break;
        case LIST:
            beginList();
            payloadSize = streamHandler.payloadSize(ctx.itemType);
            if (payloadSize >= 0) {
                bytesToSkip = payloadSize * ctx.size;
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire list of " + ctx.itemType + " (" + ctx.size + " entries)");
            } else {
                for (int i = 0; i < ctx.size; i++)
                    skipValue();
            }
            ctx.itemsRemaining = 0;
            endList();
            break;
        case OBJECT:
            beginObject();
            skippedType = nextType();
            while (skippedType != NbtType.END) {
                bytesToSkip = streamHandler.readUTFLength(in);
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire name");
                skipValue();
                skippedType = nextType();
            }
            endObject();
            break;
        case INT_ARRAY:
            beginIntArray();
            payloadSize = streamHandler.payloadSize(NbtType.INT);
            if (payloadSize >= 0) {
                bytesToSkip = payloadSize * ctx.size;
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.size + " entries)");
            } else {
                for (int i = 0; i < ctx.size; i++)
                    skipValue();
            }
            ctx.itemsRemaining = 0;
            endIntArray();
            break;
        case LONG_ARRAY:
            beginLongArray();
            payloadSize = streamHandler.payloadSize(NbtType.LONG);
            if (payloadSize >= 0) {
                bytesToSkip = payloadSize * ctx.size;
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.size + " entries)");
            } else {
                for (int i = 0; i < ctx.size; i++)
                    skipValue();
            }
            ctx.itemsRemaining = 0;
            endLongArray();
            break;
        case ROOT_LIST:
            bytesToSkip = streamHandler.readUTFLength(in);
            if (in.skip(bytesToSkip) < bytesToSkip)
                throw new IOException("Failed to skip entire name");
            beginRootList();
            skipValue();
            ctx.itemsRemaining = 0;
            endRootList();
            break;
        default:
            throw new InternalError("Unhandled tag type with non-constant payload size: " + skippedType);
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
