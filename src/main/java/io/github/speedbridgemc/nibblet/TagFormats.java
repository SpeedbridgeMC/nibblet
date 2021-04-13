package io.github.speedbridgemc.nibblet;

import io.github.speedbridgemc.nibblet.stream.StandardTagStreamHandler;
import io.github.speedbridgemc.nibblet.stream.TagStreamHandler;
import io.github.speedbridgemc.nibblet.stream.VarIntTagStreamHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

public enum TagFormats implements TagStreamHandler {
    JAVA(new StandardTagStreamHandler(ByteOrder.BIG_ENDIAN)),
    BEDROCK(new StandardTagStreamHandler(ByteOrder.LITTLE_ENDIAN)),
    BEDROCK_NETWORK(new VarIntTagStreamHandler());

    private final @NotNull TagStreamHandler delegate;

    TagFormats(@NotNull TagStreamHandler delegate) {
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