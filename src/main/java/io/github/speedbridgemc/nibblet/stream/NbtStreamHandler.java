package io.github.speedbridgemc.nibblet.stream;

import io.github.speedbridgemc.nibblet.NbtType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NbtStreamHandler {
    long payloadSize(@NotNull NbtType type);

    short readShort(@NotNull InputStream in) throws IOException;
    int readInt(@NotNull InputStream in) throws IOException;
    long readLong(@NotNull InputStream in) throws IOException;
    float readFloat(@NotNull InputStream in) throws IOException;
    double readDouble(@NotNull InputStream in) throws IOException;
    int readUTFLength(@NotNull InputStream in) throws IOException;

    void writeShort(@NotNull OutputStream out, short value) throws IOException;
    void writeInt(@NotNull OutputStream out, int value) throws IOException;
    void writeLong(@NotNull OutputStream out, long value) throws IOException;
    void writeFloat(@NotNull OutputStream out, float value) throws IOException;
    void writeDouble(@NotNull OutputStream out, double value) throws IOException;
    void writeUTFLength(@NotNull OutputStream out, int utflen) throws IOException;

    default void skip(@NotNull InputStream in, @NotNull NbtType type) throws IOException {
        long payloadSize = payloadSize(type);
        if (payloadSize <= 0)
            throw new IOException("Type " + type + " does not have a constant payload size");
        if (in.skip(payloadSize) < payloadSize)
            throw new IOException("Failed to skip entire payload of " + type);
    }
    default void skipShort(@NotNull InputStream in) throws IOException {
        try {
            skip(in, NbtType.SHORT);
        } catch (IOException e) {
            throw new IOException("Skipping not supported for " + NbtType.SHORT, e);
        }
    }
    default void skipInt(@NotNull InputStream in) throws IOException {
        try {
            skip(in, NbtType.INT);
        } catch (IOException e) {
            throw new IOException("Skipping not supported for " + NbtType.INT, e);
        }
    }
    default void skipLong(@NotNull InputStream in) throws IOException {
        try {
            skip(in, NbtType.LONG);
        } catch (IOException e) {
            throw new IOException("Skipping not supported for " + NbtType.LONG, e);
        }
    }
    default void skipFloat(@NotNull InputStream in) throws IOException {
        try {
            skip(in, NbtType.FLOAT);
        } catch (IOException e) {
            throw new IOException("Skipping not supported for " + NbtType.FLOAT, e);
        }
    }
    default void skipDouble(@NotNull InputStream in) throws IOException {
        try {
            skip(in, NbtType.DOUBLE);
        } catch (IOException e) {
            throw new IOException("Skipping not supported for " + NbtType.DOUBLE, e);
        }
    }
}
