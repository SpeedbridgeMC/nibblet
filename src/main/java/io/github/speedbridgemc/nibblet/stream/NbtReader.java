package io.github.speedbridgemc.nibblet.stream;

import io.github.speedbridgemc.nibblet.MalformedNbtException;
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
        public int itemsRemaining;

        private Context(@NotNull Mode mode, @Nullable Context next) {
            this.mode = mode;
            this.next = next;
            type = NbtType.LIST;
            itemType = NbtType.END;
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
            throw new MalformedNbtException("Unknown tag type ID " + typeId);
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
                    throw new MalformedNbtException("List or array is too small");
            } else
                throw new MalformedNbtException("Tried to read " + expectedType + " from list or array of " + ctx.itemType);
        } else {
            if (thisType == null)
                nextType();
            if (thisType != expectedType)
                throw new MalformedNbtException("Expected " + expectedType + ", got " + thisType);
            thisType = null;
        }
    }

    public void beginCompound() throws IOException {
        expectType(NbtType.OBJECT);
        ctx = ctx.push(Mode.OBJECT);
    }

    public void endCompound() throws IOException {
        if (ctx.mode != Mode.OBJECT)
            throw new MalformedNbtException("Not in a compound");
        expectType(NbtType.END);
        ctx = ctx.pop();
    }

    public void beginList() throws IOException {
        expectType(NbtType.LIST);
        ctx = ctx.push(Mode.LIST);
        ctx.type = NbtType.LIST;
        ctx.itemType = nextType();
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public @NotNull NbtType listItemType() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedNbtException("Not in a list or array");
        return ctx.itemType;
    }

    public int listSize() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedNbtException("Not in a list or array");
        return ctx.itemsRemaining;
    }

    public boolean listHasNext() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedNbtException("Not in a list or array");
        return ctx.itemsRemaining > 0;
    }

    public void endList() throws IOException {
        if (ctx.type != NbtType.LIST)
            throw new MalformedNbtException("Not in a " + NbtType.LIST);
        if (ctx.itemsRemaining > 0)
            throw new MalformedNbtException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginRootList() throws IOException {
        expectType(NbtType.ROOT_LIST);
        ctx = ctx.push(Mode.LIST);
        ctx.type = NbtType.ROOT_LIST;
        ctx.itemType = nextType();
        ctx.itemsRemaining = 1;
    }

    public void endRootList() throws IOException {
        if (ctx.type != NbtType.ROOT_LIST)
            throw new MalformedNbtException("Not in a " + NbtType.ROOT_LIST);
        if (ctx.itemsRemaining > 0)
            throw new MalformedNbtException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginByteArray() throws IOException {
        expectType(NbtType.BYTE_ARRAY);
        ctx = ctx.push(Mode.LIST);
        ctx.type = NbtType.BYTE_ARRAY;
        ctx.itemType = NbtType.BYTE;
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public void endByteArray() throws IOException {
        if (ctx.type != NbtType.BYTE_ARRAY)
            throw new MalformedNbtException("Not in a " + NbtType.BYTE_ARRAY);
        if (ctx.itemsRemaining > 0)
            throw new MalformedNbtException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginIntArray() throws IOException {
        expectType(NbtType.INT_ARRAY);
        ctx = ctx.push(Mode.LIST);
        ctx.type = NbtType.INT_ARRAY;
        ctx.itemType = NbtType.INT;
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public void endIntArray() throws IOException {
        if (ctx.type != NbtType.INT_ARRAY)
            throw new MalformedNbtException("Not in a " + NbtType.INT_ARRAY);
        if (ctx.itemsRemaining > 0)
            throw new MalformedNbtException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginLongArray() throws IOException {
        expectType(NbtType.LONG_ARRAY);
        ctx = ctx.push(Mode.LIST);
        ctx.type = NbtType.LONG_ARRAY;
        ctx.itemType = NbtType.LONG;
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public void endLongArray() throws IOException {
        if (ctx.type != NbtType.LONG_ARRAY)
            throw new MalformedNbtException("Not in a " + NbtType.LONG_ARRAY);
        if (ctx.itemsRemaining > 0)
            throw new MalformedNbtException("Expected end of list or array");
        ctx = ctx.pop();
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
        if (ctx.mode == Mode.LIST)
            skippedType = ctx.itemType;
        else if (thisType != null) {
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
            bytesToSkip = ctx.itemsRemaining;
            if (in.skip(bytesToSkip) < bytesToSkip)
                throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.itemsRemaining + " entries)");
            ctx.itemsRemaining = 0;
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
                bytesToSkip = payloadSize * ctx.itemsRemaining;
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire list of " + ctx.itemType + " (" + ctx.itemsRemaining + " entries)");
            } else {
                int itemsRemaining = ctx.itemsRemaining;
                for (int i = 0; i < itemsRemaining; i++)
                    skipValue();
            }
            ctx.itemsRemaining = 0;
            endList();
            break;
        case OBJECT:
            beginCompound();
            skippedType = nextType();
            while (skippedType != NbtType.END) {
                bytesToSkip = streamHandler.readUTFLength(in);
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire name");
                skipValue();
                skippedType = nextType();
            }
            endCompound();
            break;
        case INT_ARRAY:
            beginIntArray();
            payloadSize = streamHandler.payloadSize(NbtType.INT);
            if (payloadSize >= 0) {
                bytesToSkip = payloadSize * ctx.itemsRemaining;
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.itemsRemaining + " entries)");
            } else {
                int itemsRemaining = ctx.itemsRemaining;
                for (int i = 0; i < itemsRemaining; i++)
                    skipValue();
            }
            ctx.itemsRemaining = 0;
            endIntArray();
            break;
        case LONG_ARRAY:
            beginLongArray();
            payloadSize = streamHandler.payloadSize(NbtType.LONG);
            if (payloadSize >= 0) {
                bytesToSkip = payloadSize * ctx.itemsRemaining;
                if (in.skip(bytesToSkip) < bytesToSkip)
                    throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.itemsRemaining + " entries)");
            } else {
                int itemsRemaining = ctx.itemsRemaining;
                for (int i = 0; i < itemsRemaining; i++)
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
