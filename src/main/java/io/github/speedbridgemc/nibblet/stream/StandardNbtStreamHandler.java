package io.github.speedbridgemc.nibblet.stream;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StandardNbtStreamHandler implements NbtStreamHandler {
    protected final ByteOrder byteOrder;
    private final ThreadLocal<ByteBuffer> tlScratchBuf;

    public StandardNbtStreamHandler(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        tlScratchBuf = ThreadLocal.withInitial(() -> {
            ByteBuffer buf = ByteBuffer.allocate(Double.BYTES);
            buf.order(byteOrder);
            return buf;
        });
    }

    protected final @NotNull ByteBuffer scratchBuf() {
        return tlScratchBuf.get();
    }

    @Override
    public short readShort(@NotNull InputStream in) throws IOException {
        ByteBuffer buf = scratchBuf();
        if (in.read(buf.array(), 0, Short.BYTES) < Short.BYTES)
            throw new IOException("Failed to read entire short");
        return buf.getShort(0);
    }

    @Override
    public int readInt(@NotNull InputStream in) throws IOException {
        ByteBuffer buf = scratchBuf();
        if (in.read(buf.array(), 0, Integer.BYTES) < Integer.BYTES)
            throw new IOException("Failed to read entire int");
        return buf.getInt(0);
    }

    @Override
    public long readLong(@NotNull InputStream in) throws IOException {
        ByteBuffer buf = scratchBuf();
        if (in.read(buf.array(), 0, Long.BYTES) < Long.BYTES)
            throw new IOException("Failed to read entire long");
        return buf.getLong(0);
    }

    @Override
    public float readFloat(@NotNull InputStream in) throws IOException {
        ByteBuffer buf = scratchBuf();
        if (in.read(buf.array(), 0, Float.BYTES) < Float.BYTES)
            throw new IOException("Failed to read entire float");
        return buf.getFloat(0);
    }

    @Override
    public double readDouble(@NotNull InputStream in) throws IOException {
        ByteBuffer buf = scratchBuf();
        if (in.read(buf.array(), 0, Double.BYTES) < Double.BYTES)
            throw new IOException("Failed to read entire double");
        return buf.getDouble(0);
    }

    @Override
    public int readUTFLength(@NotNull InputStream in) throws IOException {
        ByteBuffer buf = scratchBuf();
        if (in.read(buf.array(), 0, Short.BYTES) < Short.BYTES)
            throw new IOException("Failed to read entire UTF length");
        if (byteOrder == ByteOrder.LITTLE_ENDIAN)
            return buf.get(1) << 8 | buf.get(0);
        else
            return buf.get(0) << 8 | buf.get(1);
    }

    @Override
    public void writeShort(@NotNull OutputStream out, short value) throws IOException {
        ByteBuffer buf = scratchBuf();
        buf.putShort(0, value);
        out.write(buf.array(), 0, Short.BYTES);
    }

    @Override
    public void writeInt(@NotNull OutputStream out, int value) throws IOException {
        ByteBuffer buf = scratchBuf();
        buf.putInt(0, value);
        out.write(buf.array(), 0, Integer.BYTES);
    }

    @Override
    public void writeLong(@NotNull OutputStream out, long value) throws IOException {
        ByteBuffer buf = scratchBuf();
        buf.putLong(0, value);
        out.write(buf.array(), 0, Long.BYTES);
    }

    @Override
    public void writeFloat(@NotNull OutputStream out, float value) throws IOException {
        ByteBuffer buf = scratchBuf();
        buf.putFloat(0, value);
        out.write(buf.array(), 0, Float.BYTES);
    }

    @Override
    public void writeDouble(@NotNull OutputStream out, double value) throws IOException {
        ByteBuffer buf = scratchBuf();
        buf.putDouble(0, value);
        out.write(buf.array(), 0, Double.BYTES);
    }

    @Override
    public void writeUTFLength(@NotNull OutputStream out, int utflen) throws IOException {
        if (utflen > 0xFFFF)
            throw new UTFDataFormatException("String is too big");
        ByteBuffer buf = scratchBuf();
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            buf.put(0, (byte) (utflen & 0xFF));
            buf.put(1, (byte) ((utflen << 8) & 0xFF));
        } else {
            buf.put(0, (byte) ((utflen << 8) & 0xFF));
            buf.put(1, (byte) (utflen & 0xFF));
        }
        out.write(buf.array(), 0, Short.BYTES);
    }
}
