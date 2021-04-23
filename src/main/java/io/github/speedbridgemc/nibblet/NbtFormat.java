package io.github.speedbridgemc.nibblet;

import io.github.speedbridgemc.nibblet.stream.BedrockNetworkNbtStreamHandler;
import io.github.speedbridgemc.nibblet.stream.StandardNbtStreamHandler;
import io.github.speedbridgemc.nibblet.stream.NbtStreamHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

/**
 * Represents common NBT structure formats.
 */
public enum NbtFormat implements NbtStreamHandler {
    /**
     * The "classic" format used by Java Edition. Big-endian values.
     */
    JAVA(new StandardNbtStreamHandler(ByteOrder.BIG_ENDIAN)),
    /**
     * The format used by Bedrock Edition. Little-endian values.
     */
    BEDROCK(new StandardNbtStreamHandler(ByteOrder.LITTLE_ENDIAN)),
    /**
     * The format used by Bedrock Edition's networking code. Not used for files, obviously.
     * <ul>
     *     <li><a href="https://developers.google.com/protocol-buffers/docs/encoding#signed_integers">ZigZag</a>
     *     VarInt encoding for {@code TAG_Int} values.</li>
     *     <li><a href="https://developers.google.com/protocol-buffers/docs/encoding#signed_integers">ZigZag</a>
     *     VarLong encoding for {@code TAG_Long} values.</li>
     *      <li>VarInt encoding for string length prefixes (names and {@code TAG_String} values).</li>
     *      <li>Little-endian encoding for all other values.</li>
     * </ul>
     */
    BEDROCK_NETWORK(new BedrockNetworkNbtStreamHandler());

    private final @NotNull NbtStreamHandler delegate;

    NbtFormat(@NotNull NbtStreamHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public short readShort(@NotNull InputStream in) throws IOException {
        return delegate.readShort(in);
    }

    @Override
    public int readInt(@NotNull InputStream in) throws IOException {
        return delegate.readInt(in);
    }

    @Override
    public long readLong(@NotNull InputStream in) throws IOException {
        return delegate.readLong(in);
    }

    @Override
    public float readFloat(@NotNull InputStream in) throws IOException {
        return delegate.readFloat(in);
    }

    @Override
    public double readDouble(@NotNull InputStream in) throws IOException {
        return delegate.readDouble(in);
    }

    @Override
    public int readUTFLength(@NotNull InputStream in) throws IOException {
        return delegate.readUTFLength(in);
    }

    @Override
    public void writeShort(@NotNull OutputStream out, short value) throws IOException {
        delegate.writeShort(out, value);
    }

    @Override
    public void writeInt(@NotNull OutputStream out, int value) throws IOException {
        delegate.writeInt(out, value);
    }

    @Override
    public void writeLong(@NotNull OutputStream out, long value) throws IOException {
        delegate.writeLong(out, value);
    }

    @Override
    public void writeFloat(@NotNull OutputStream out, float value) throws IOException {
        delegate.writeFloat(out, value);
    }

    @Override
    public void writeDouble(@NotNull OutputStream out, double value) throws IOException {
        delegate.writeDouble(out, value);
    }

    @Override
    public void writeUTFLength(@NotNull OutputStream out, int utflen) throws IOException {
        delegate.writeUTFLength(out, utflen);
    }
}
