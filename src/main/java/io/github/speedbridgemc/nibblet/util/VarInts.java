package io.github.speedbridgemc.nibblet.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides utility methods for reading and writing VarInts.
 */
public final class VarInts {
    private VarInts() { }

    /**
     * Reads an {@code int} value encoded as a VarInt from an input stream.
     * @param in input stream
     * @return resulting value
     * @throws IOException if an I/O error occurs.
     */
    public static int readVarInt(@NotNull InputStream in) throws IOException {
        int result = 0, shift = 0;
        int b;
        do {
            if (shift >= 32)
                throw new IOException("VarInt is too long!");
            b = in.read();
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    /**
     * Writes an {@code int} value encoded as a VarInt to an output stream.
     * @param out output stream
     * @param value value to encode
     * @throws IOException if an I/O error occurs.
     */
    public static void writeVarInt(@NotNull OutputStream out, int value) throws IOException {
        do {
            int bits = value & 0x7F;
            value >>>= 7;
            int b = bits | ((value == 0 ? 0 : 0x80));
            out.write(b);
        } while (value != 0);
    }

    /**
     * Reads a {@code long} value encoded as a VarLong from an input stream.
     * @param in input stream
     * @return resulting value
     * @throws IOException if an I/O error occurs.
     */
    public static long readVarLong(@NotNull InputStream in) throws IOException {
        long result = 0;
        int shift = 0;
        int b;
        do {
            if (shift >= 64)
                throw new IOException("VarLong is too long!");
            b = in.read();
            result |= (long) (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    /**
     * Writes a {@code long} value encoded as a VarLong to an output stream.
     * @param out output stream
     * @param value value to encode
     * @throws IOException if an I/O error occurs.
     */
    public static void writeVarLong(@NotNull OutputStream out, long value) throws IOException {
        do {
            int bits = (int) (value & 0x7F);
            value >>>= 7;
            int b = bits | ((value == 0 ? 0 : 0x80));
            out.write(b);
        } while (value != 0);
    }

    /**
     * Reads an {@code int} value encoded as a
     * <a href="https://developers.google.com/protocol-buffers/docs/encoding#signed_integers">ZigZag</a> VarInt
     * from an input stream.
     * @param in input stream
     * @return resulting value
     * @throws IOException if an I/O error occurs.
     */
    public static int readVarIntZigZag(@NotNull InputStream in) throws IOException {
        int value = readVarInt(in);
        return (value >>> 1) ^ (-(value & 1));
    }

    /**
     * Writes an {@code int} value encoded as a
     * <a href="https://developers.google.com/protocol-buffers/docs/encoding#signed_integers">ZigZag</a> VarInt
     * to an output stream.
     * @param out output stream
     * @param value value to encode
     * @throws IOException if an I/O error occurs.
     */
    public static void writeVarIntZigZag(@NotNull OutputStream out, int value) throws IOException {
        writeVarInt(out, (value << 1) ^ (value >> 31));
    }

    /**
     * Reads a {@code long} value encoded as a
     * <a href="https://developers.google.com/protocol-buffers/docs/encoding#signed_integers">ZigZag</a> VarLong
     * from an input stream.
     * @param in input stream
     * @return resulting value
     * @throws IOException if an I/O error occurs.
     */
    public static long readVarLongZigZag(@NotNull InputStream in) throws IOException {
        long value = readVarLong(in);
        return (value >>> 1) ^ (-(value & 1));
    }

    /**
     * Writes a {@code long} value encoded as a
     * <a href="https://developers.google.com/protocol-buffers/docs/encoding#signed_integers">ZigZag</a> VarLong
     * to an output stream.
     * @param out output stream
     * @param value value to encode
     * @throws IOException if an I/O error occurs.
     */
    public static void writeVarLongZigZag(@NotNull OutputStream out, long value) throws IOException {
        writeVarLong(out, (value << 1) ^ (value >> 63));
    }
}
