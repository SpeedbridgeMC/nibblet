package io.github.speedbridgemc.nibblet.stream;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TagStreamHandler {
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
}
