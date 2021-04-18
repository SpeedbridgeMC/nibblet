package io.github.speedbridgemc.nibblet.stream;

import io.github.speedbridgemc.nibblet.MalformedTagException;
import io.github.speedbridgemc.nibblet.TagType;
import io.github.speedbridgemc.nibblet.util.ModUTF8Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public final class TagReader implements Closeable {
    private static final ThreadLocal<byte[]> TL_BUFFER = new ThreadLocal<>();

    private static byte @NotNull [] buffer(int length) {
        byte[] buf = TL_BUFFER.get();
        if (buf == null || buf.length < length) {
            buf = new byte[length];
            TL_BUFFER.set(buf);
        }
        return buf;
    }

    private final @NotNull TagStreamHandler streamHandler;
    private final @NotNull InputStream in;
    private @Nullable TagType thisType;
    private boolean firstByte;
    private @NotNull Context ctx;
    
    private enum Mode {
        ROOT,
        COMPOUND,
        LIST
    }

    private static final class Context {
        public final @NotNull Mode mode;
        private final @Nullable Context next;
        public @NotNull TagType type;
        public @NotNull TagType itemType;
        public int itemsRemaining;

        private Context(@NotNull Mode mode, @Nullable Context next) {
            this.mode = mode;
            this.next = next;
            type = TagType.LIST;
            itemType = TagType.END;
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

    public TagReader(@NotNull TagStreamHandler streamHandler, @NotNull InputStream in) {
        this.streamHandler = streamHandler;
        this.in = in;
        firstByte = true;
        ctx = new Context(Mode.ROOT, null);
    }

    public @NotNull TagType nextType() throws IOException {
        byte typeId = (byte) (in.read() & 0xFF);
        TagType type = TagType.fromId(typeId);
        if (type == null)
            throw new MalformedTagException("Unknown tag type ID " + typeId);
        if (firstByte) {
            firstByte = false;
            if (type == TagType.LIST)
                type = TagType.ROOT_LIST;
        }
        thisType = type;
        return type;
    }

    private void expectType(@NotNull TagType expectedType) throws IOException {
        if (ctx.mode == Mode.LIST) {
            if (ctx.itemType == expectedType) {
                if (--ctx.itemsRemaining < 0)
                    throw new MalformedTagException("List or array is too small");
            } else
                throw new MalformedTagException("Tried to read " + expectedType + " from list or array of " + ctx.itemType);
            firstByte = false;
        } else {
            if (thisType == null)
                nextType();
            if (thisType != expectedType)
                throw new MalformedTagException("Expected " + expectedType + ", got " + thisType);
            thisType = null;
        }
    }

    public void beginCompound() throws IOException {
        expectType(TagType.COMPOUND);
        ctx = ctx.push(Mode.COMPOUND);
    }

    public void endCompound() throws IOException {
        if (ctx.mode != Mode.COMPOUND)
            throw new MalformedTagException("Not in a compound");
        expectType(TagType.END);
        ctx = ctx.pop();
    }

    public void beginList() throws IOException {
        expectType(TagType.LIST);
        ctx = ctx.push(Mode.LIST);
        ctx.type = TagType.LIST;
        ctx.itemType = nextType();
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public @NotNull TagType listItemType() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedTagException("Not in a list or array");
        return ctx.itemType;
    }

    public int listSize() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedTagException("Not in a list or array");
        return ctx.itemsRemaining;
    }

    public boolean listHasNext() throws IOException {
        if (ctx.mode != Mode.LIST)
            throw new MalformedTagException("Not in a list or array");
        return ctx.itemsRemaining > 0;
    }

    public void endList() throws IOException {
        if (ctx.type != TagType.LIST)
            throw new MalformedTagException("Not in a " + TagType.LIST);
        if (ctx.itemsRemaining > 0)
            throw new MalformedTagException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginRootList() throws IOException {
        expectType(TagType.ROOT_LIST);
        ctx = ctx.push(Mode.LIST);
        ctx.type = TagType.ROOT_LIST;
        ctx.itemType = nextType();
        ctx.itemsRemaining = 1;
    }

    public void endRootList() throws IOException {
        if (ctx.type != TagType.ROOT_LIST)
            throw new MalformedTagException("Not in a " + TagType.ROOT_LIST);
        if (ctx.itemsRemaining > 0)
            throw new MalformedTagException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginByteArray() throws IOException {
        expectType(TagType.BYTE_ARRAY);
        ctx = ctx.push(Mode.LIST);
        ctx.type = TagType.BYTE_ARRAY;
        ctx.itemType = TagType.BYTE;
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public void endByteArray() throws IOException {
        if (ctx.type != TagType.BYTE_ARRAY)
            throw new MalformedTagException("Not in a " + TagType.BYTE_ARRAY);
        if (ctx.itemsRemaining > 0)
            throw new MalformedTagException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginIntArray() throws IOException {
        expectType(TagType.INT_ARRAY);
        ctx = ctx.push(Mode.LIST);
        ctx.type = TagType.INT_ARRAY;
        ctx.itemType = TagType.INT;
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public void endIntArray() throws IOException {
        if (ctx.type != TagType.INT_ARRAY)
            throw new MalformedTagException("Not in a " + TagType.INT_ARRAY);
        if (ctx.itemsRemaining > 0)
            throw new MalformedTagException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginLongArray() throws IOException {
        expectType(TagType.LONG_ARRAY);
        ctx = ctx.push(Mode.LIST);
        ctx.type = TagType.LONG_ARRAY;
        ctx.itemType = TagType.LONG;
        ctx.itemsRemaining = streamHandler.readInt(in);
    }

    public void endLongArray() throws IOException {
        if (ctx.type != TagType.LONG_ARRAY)
            throw new MalformedTagException("Not in a " + TagType.LONG_ARRAY);
        if (ctx.itemsRemaining > 0)
            throw new MalformedTagException("Expected end of list or array");
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
        return ModUTF8Strings.decode(buf, utflen);
    }

    public byte nextByte() throws IOException {
        expectType(TagType.BYTE);
        return (byte) (in.read() & 0xFF);
    }

    public boolean nextBoolean() throws IOException {
        return nextByte() > 0;
    }

    public short nextShort() throws IOException {
        expectType(TagType.SHORT);
        return streamHandler.readShort(in);
    }

    public int nextInt() throws IOException {
        expectType(TagType.INT);
        return streamHandler.readInt(in);
    }

    public long nextLong() throws IOException {
        expectType(TagType.LONG);
        return streamHandler.readLong(in);
    }

    public float nextFloat() throws IOException {
        expectType(TagType.FLOAT);
        return streamHandler.readFloat(in);
    }

    public double nextDouble() throws IOException {
        expectType(TagType.DOUBLE);
        return streamHandler.readDouble(in);
    }

    public @NotNull String nextString() throws IOException {
        expectType(TagType.STRING);
        return nextName();
    }

    public void skipValue() throws IOException {
        TagType skippedType;
        if (ctx.mode == Mode.LIST)
            skippedType = ctx.itemType;
        else if (thisType != null)
            skippedType = thisType;
        else
            skippedType = nextType();
        if (skippedType.hasConstantPayloadSize()) {
            final long payloadSize = skippedType.payloadSize();
            if (in.skip(payloadSize) < payloadSize)
                throw new IOException("Failed to skip entire " + skippedType + " value");
            return;
        }
        long bytesToSkip;
        switch (skippedType) {
        case BYTE_ARRAY:
            beginByteArray();
            bytesToSkip = TagType.BYTE.payloadSize() * ctx.itemsRemaining;
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
            if (ctx.itemType.hasConstantPayloadSize()) {
                bytesToSkip = ctx.itemType.payloadSize() * ctx.itemsRemaining;
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
        case COMPOUND:
            beginCompound();
            skippedType = nextType();
            while (skippedType != TagType.END) {
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
            bytesToSkip = TagType.INT.payloadSize() * ctx.itemsRemaining;
            if (in.skip(bytesToSkip) < bytesToSkip)
                throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.itemsRemaining + " entries)");
            ctx.itemsRemaining = 0;
            endIntArray();
            break;
        case LONG_ARRAY:
            beginLongArray();
            bytesToSkip = TagType.LONG.payloadSize() * ctx.itemsRemaining;
            if (in.skip(bytesToSkip) < bytesToSkip)
                throw new IOException("Failed to skip entire " + ctx.type + " (" + ctx.itemsRemaining + " entries)");
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
