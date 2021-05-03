package io.github.speedbridgemc.nibblet.util;

import org.jetbrains.annotations.NotNull;

import java.io.UTFDataFormatException;

/**
 * Provides utility methods for encoding and decoding strings using Java's {@linkplain java.io.DataInput modified UTF-8 encoding}.
 */
public final class MUTF8Strings {
    private MUTF8Strings() { }

    /**
     * Decodes a string encoded in the modified UTF-8 format.
     * @param src input buffer
     * @param utflen length of input
     * @param dest destination buffer
     * @throws UTFDataFormatException if malformed input is received.
     */
    public static void decode(byte @NotNull [] src, int utflen, @NotNull StringBuilder dest) throws UTFDataFormatException {
        if (src.length < utflen)
            throw new IllegalArgumentException("Length of input is greater than input buffer's length");

        int count = 0;
        int c1, c2, c3;

        while (count < utflen) {
            c1 = (int) src[count] & 0xFF;
            if (c1 > 127) break;
            count++;
            dest.append((char) c1);
        }
        
        while (count < utflen) {
            c1 = (int) src[count] & 0xFF;
            int m = c1 >> 4;
            if (m <= 7) {
                // 0b0xxxxxxx
                count++;
                dest.append((char) c1);
            } else if (m == 12 || m == 13) {
                // 0b110xxxxx 0b10xxxxxx
                count += 2;
                if (count > utflen)
                    throw new UTFDataFormatException("Malformed input: Partial 2-byte character at end");
                c2 = src[count - 1];
                if ((c2 & 0xC0) != 0x80)
                    throw new UTFDataFormatException("Malformed input around byte " + count);
                dest.append((char) (((c1 & 0x1F) << 6) | (c2 & 0x3F)));
            } else if (m == 14) {
                // 0b1110xxxx 0b10xxxxxx 0b10xxxxxx
                count += 3;
                if (count > utflen)
                    throw new UTFDataFormatException("Malformed input: Partial 3-byte character at end");
                c2 = src[count - 2];
                c3 = src[count - 1];
                if ((c2 & 0xC0) != 0x80 || (c3 & 0xC0) != 0x80)
                    throw new UTFDataFormatException("Malformed input around byte " + (count - 1));
                dest.append((char) (((c1 & 0x0F) << 12) | ((c2 & 0x3F) << 6) | (c3 & 0x3F)));
            } else
                throw new UTFDataFormatException("Malformed input around byte " + count);
        }
    }

    private static final ThreadLocal<StringBuilder> TL_STRING_BUILDER = ThreadLocal.withInitial(StringBuilder::new);

    private static @NotNull StringBuilder stringBuilder() {
        StringBuilder sb = TL_STRING_BUILDER.get();
        sb.setLength(0);
        return sb;
    }

    /**
     * Decodes a string encoded in the modified UTF-8 format.
     * @param src input buffer
     * @param utflen length of input
     * @return decoded string
     * @throws UTFDataFormatException if malformed input is received.
     */
    public static @NotNull String decode(byte @NotNull [] src, int utflen) throws UTFDataFormatException {
        StringBuilder sb = stringBuilder();
        decode(src, utflen, sb);
        return sb.toString();
    }

    private static final ThreadLocal<byte[]> TL_BUFFER = new ThreadLocal<>();

    private static byte @NotNull [] buffer(int length) {
        byte[] buf = TL_BUFFER.get();
        if (buf == null || buf.length < length) {
            buf = new byte[length];
            TL_BUFFER.set(buf);
        }
        return buf;
    }

    /**
     * Stores {@linkplain #encode(String) encode operation} results.
     */
    public static final class EncodeResult {
        private final int utflen;
        private final byte @NotNull [] buf;

        private EncodeResult(int utflen, byte @NotNull [] buf) {
            this.utflen = utflen;
            this.buf = buf;
        }

        /**
         * Gets the length of the encoded data in bytes.
         * @return UTF length
         */
        public int utfLength() {
            return utflen;
        }

        /**
         * Gets the buffer containing the encoded bytes.
         * @return buffer
         */
        public byte @NotNull [] buffer() {
            return buf;
        }

        /**
         * Copies the buffer containing the encoding bytes.<p>
         * If you intend to persist the encoded bytes, persist the result of this method instead!
         * @return buffer copy
         */
        public byte @NotNull [] copyBuffer() {
            byte[] copy = new byte[utflen];
            System.arraycopy(buf, 0, copy, 0, utflen);
            return copy;
        }
    }

    /**
     * Encodes a string into the modified UTF-8 format.
     * @param value string
     * @return encode result
     */
    public static @NotNull EncodeResult encode(@NotNull String value) {
        final int strlen = value.length();
        int utflen = 0;
        char c;

        for (int i = 0; i < strlen; i++) {
            c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F))
                utflen++;
            else if (c > 0x07FF)
                utflen += 3;
            else
                utflen += 2;
        }

        byte[] buf = buffer(utflen);

        int count = 0, i;

        for (i = 0; i < strlen; i++) {
            c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F))
                buf[count++] = (byte) c;
            else
                break;
        }

        for (; i < strlen; i++){
            c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F))
                buf[count++] = (byte) c;
            else if (c > 0x07FF) {
                buf[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buf[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buf[count++] = (byte) (0x80 | (c & 0x3F));
            } else {
                buf[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                buf[count++] = (byte) (0x80 | (c & 0x3F));
            }
        }

        return new EncodeResult(utflen, buf);
    }
}
