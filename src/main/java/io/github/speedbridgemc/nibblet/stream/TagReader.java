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

    private static final class Context {
        private final @Nullable Context next;
        public boolean isList;
        public @NotNull TagType type;
        public @NotNull TagType itemType;
        public int itemsRemaining;

        private Context(@Nullable Context next) {
            this.next = next;
            isList = false;
            type = TagType.LIST;
            itemType = TagType.END;
            itemsRemaining = 0;
        }
        
        public @NotNull Context push() {
            return new Context(this);
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
        ctx = new Context(null);
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
        if (thisType == null)
            thisType = nextType();
        if (thisType != TagType.COMPOUND)
            throw new MalformedTagException("Expected " + expectedType + ", got " + thisType);
        thisType = null;
    }
    
    public void beginCompound() throws IOException {
        expectType(TagType.COMPOUND);
    }
    
    public void endCompound() throws IOException {
        expectType(TagType.END);
    }

    public void beginList() throws IOException {
        expectType(TagType.LIST);
        ctx = ctx.push();
        ctx.isList = true;
        ctx.type = TagType.LIST;
        ctx.itemType = nextType();
        ctx.itemsRemaining = nextInt();
    }

    public @NotNull TagType listItemType() throws IOException {
        if (!ctx.isList)
            throw new MalformedTagException("Not in a list or array");
        return ctx.itemType;
    }

    public int listSize() throws IOException {
        if (!ctx.isList)
            throw new MalformedTagException("Not in a list or array");
        return ctx.itemsRemaining;
    }

    public boolean listHasNext() throws IOException {
        if (!ctx.isList)
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
        ctx = ctx.push();
        ctx.isList = true;
        ctx.type = TagType.ROOT_LIST;
        ctx.itemType = TagType.LIST;
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
        ctx = ctx.push();
        ctx.isList = true;
        ctx.type = TagType.BYTE_ARRAY;
        ctx.itemType = TagType.BYTE;
        ctx.itemsRemaining = nextInt();
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
        ctx = ctx.push();
        ctx.isList = true;
        ctx.type = TagType.INT_ARRAY;
        ctx.itemType = TagType.INT;
        ctx.itemsRemaining = nextInt();
    }

    public void endIntArray() throws IOException {
        if (ctx.type != TagType.BYTE_ARRAY)
            throw new MalformedTagException("Not in a " + TagType.BYTE_ARRAY);
        if (ctx.itemsRemaining > 0)
            throw new MalformedTagException("Expected end of list or array");
        ctx = ctx.pop();
    }

    public void beginLongArray() throws IOException {
        expectType(TagType.LONG_ARRAY);
        ctx = ctx.push();
        ctx.isList = true;
        ctx.type = TagType.LONG_ARRAY;
        ctx.itemType = TagType.LONG;
        ctx.itemsRemaining = nextInt();
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
        byte[] buf = buffer(utflen);
        if (in.read(buf, 0, utflen) < utflen)
            throw new IOException("Failed to read entire string");
        return ModUTF8Strings.decode(buf, utflen);
    }

    private void nextItem(@NotNull TagType type) throws IOException {
        if (ctx.isList && ctx.itemType == type) {
            if (--ctx.itemsRemaining < 0)
                throw new MalformedTagException("List or array is too small");
        } else if (ctx.isList)
            throw new MalformedTagException("Tried to read byte from list or array of " + ctx.itemType);
        else
            expectType(type);
        firstByte = false;
    }

    public byte nextByte() throws IOException {
        nextItem(TagType.BYTE);
        return (byte) (in.read() & 0xFF);
    }

    public short nextShort() throws IOException {
        nextItem(TagType.SHORT);
        return streamHandler.readShort(in);
    }

    public int nextInt() throws IOException {
        nextItem(TagType.INT);
        return streamHandler.readInt(in);
    }

    public long nextLong() throws IOException {
        nextItem(TagType.LONG);
        return streamHandler.readLong(in);
    }

    public float nextFloat() throws IOException {
        nextItem(TagType.FLOAT);
        return streamHandler.readFloat(in);
    }

    public double nextDouble() throws IOException {
        nextItem(TagType.DOUBLE);
        return streamHandler.readDouble(in);
    }

    public @NotNull String nextString() throws IOException {
        nextItem(TagType.STRING);
        return nextName();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
