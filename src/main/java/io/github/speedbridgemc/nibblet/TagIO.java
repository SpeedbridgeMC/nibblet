package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Provides methods to read and write NBT binaries.
 */
public final class TagIO {
    private TagIO() { }

    /**
     * Represents the result of a {@link #read(DataInput)} operation.
     */
    public static final class Result {
        private final @NotNull String rootName;
        private final @NotNull CompoundTag rootTag;

        private Result(@NotNull String rootName, @NotNull CompoundTag rootTag) {
            this.rootName = rootName;
            this.rootTag = rootTag;
        }

        /**
         * Gets the name of the root {@link CompoundTag}.
         * @return root tag name
         */
        public @NotNull String rootName() {
            return rootName;
        }

        /**
         * Gets the root {@link CompoundTag}.
         * @return root tag
         */
        public @NotNull CompoundTag rootTag() {
            return rootTag;
        }
    }

    /**
     * Deserializes an NBT structure from binary data.
     * @param in input
     * @return {@linkplain Result read result}
     * @throws MalformedTagException if the tag data is malformed
     * @throws IOException if an I/O error occurs
     */
    public static @NotNull Result read(@NotNull DataInput in) throws IOException {
        if (in.readByte() != TagType.COMPOUND.id())
            throw new MalformedTagException("Root tag is not a compound tag");
        String rootName = in.readUTF();
        CompoundTag rootTag = readCompound(in);
        return new Result(rootName, rootTag);
    }

    private static @NotNull CompoundTag readCompound(@NotNull DataInput in) throws IOException {
        CompoundTag.Builder builder = CompoundTag.builder();
        while (true) {
            byte typeId = in.readByte();
            TagType type = TagType.fromId(typeId);
            if (type == null)
                throw new MalformedTagException("Unknown type ID " + typeId);
            if (type == TagType.END)
                break;
            String name = in.readUTF();
            builder.put(name, readSingle(type, in));
        }
        return builder.build();
    }
    
    private static @NotNull Tag readSingle(@NotNull TagType type, @NotNull DataInput in) throws IOException {
        switch (type) {
        case BYTE:
            return ByteTag.of(in.readByte());
        case SHORT:
            return ShortTag.of(in.readShort());
        case INT:
            return IntTag.of(in.readInt());
        case LONG:
            return LongTag.of(in.readLong());
        case FLOAT:
            return FloatTag.of(in.readFloat());
        case DOUBLE:
            return DoubleTag.of(in.readDouble());
        case BYTE_ARRAY:
            int baSize = in.readInt();
            byte[] baValues = new byte[baSize];
            for (int i = 0; i < baSize; i++)
                baValues[i] = in.readByte();
            return new ByteArrayTag(baValues);
        case STRING:
            return StringTag.of(in.readUTF());
        case COMPOUND:
            return readCompound(in);
        case LIST:
            byte lTagTypeId = in.readByte();
            TagType lTagType = TagType.fromId(lTagTypeId);
            if (lTagType == null)
                throw new MalformedTagException("Unknown type ID " + lTagTypeId);
            int lSize = in.readInt();
            ListTag.Builder lBuilder = ListTag.builder(lSize);
            for (int i = 0; i < lSize; i++)
                lBuilder.add(readSingle(lTagType, in));
            return lBuilder.build();
        case INT_ARRAY:
            int iaSize = in.readInt();
            int[] iaValues = new int[iaSize];
            for (int i = 0; i < iaSize; i++)
                iaValues[i] = in.readInt();
            return new IntArrayTag(iaValues);
        case LONG_ARRAY:
            int laSize = in.readInt();
            long[] laValues = new long[laSize];
            for (int i = 0; i < laSize; i++)
                laValues[i] = in.readLong();
            return new LongArrayTag(laValues);
        default:
            throw new InternalError("Unhandled type " + type + "!");
        }
    }

    /**
     * Serializes an NBT structure into binary data.
     * @param rootName name of root tag
     * @param rootTag root tag
     * @param out output
     * @throws IOException if an I/O error occurs
     */
    public static void write(@NotNull String rootName, @NotNull CompoundTag rootTag, @NotNull DataOutput out) throws IOException {
        out.writeByte(TagType.COMPOUND.id());
        out.writeUTF(rootName);
        writeCompound(rootTag, out);
    }

    private static void writeCompound(@NotNull CompoundTag tag, @NotNull DataOutput out) throws IOException {
        for (Map.Entry<String, Tag> entry : tag.entries()) {
            Tag value = entry.getValue();
            TagType type = value.type();
            out.writeByte(type.id());
            out.writeUTF(entry.getKey());
            writeSingle(type, value, out);
        }
        out.writeByte(TagType.END.id());
    }

    private static void writeSingle(@NotNull TagType type, @NotNull Tag value, @NotNull DataOutput out) throws IOException {
        switch (type) {
        case BYTE:
            out.writeByte(((ByteTag) value).value());
            break;
        case SHORT:
            out.writeShort(((ShortTag) value).value());
            break;
        case INT:
            out.writeInt(((IntTag) value).value());
            break;
        case LONG:
            out.writeLong(((LongTag) value).value());
            break;
        case FLOAT:
            out.writeFloat(((FloatTag) value).value());
            break;
        case DOUBLE:
            out.writeDouble(((DoubleTag) value).value());
            break;
        case BYTE_ARRAY:
            ByteArrayTag baTag = (ByteArrayTag) value;
            int baSize = baTag.length();
            out.writeInt(baSize);
            for (int i = 0; i < baSize; i++)
                out.writeByte(baTag.get(i));
            break;
        case STRING:
            out.writeUTF(((StringTag) value).value());
            break;
        case COMPOUND:
            writeCompound((CompoundTag) value, out);
            break;
        case LIST:
            ListTag lTag = (ListTag) value;
            TagType lItemType = lTag.itemType();
            out.writeByte(lItemType.id());
            int lSize = lTag.size();
            out.writeInt(lSize);
            for (int i = 0; i < lSize; i++)
                writeSingle(lItemType, lTag.get(i), out);
            break;
        case INT_ARRAY:
            IntArrayTag iaTag = (IntArrayTag) value;
            int iaSize = iaTag.length();
            out.writeInt(iaSize);
            for (int i = 0; i < iaSize; i++)
                out.writeInt(iaTag.get(i));
            break;
        case LONG_ARRAY:
            LongArrayTag laTag = (LongArrayTag) value;
            int laSize = laTag.length();
            out.writeInt(laSize);
            for (int i = 0; i < laSize; i++)
                out.writeLong(laTag.get(i));
            break;
        default:
            throw new InternalError("Unhandled type " + type + "!");
        }
    }

    /**
     * Deserializes an NBT structure from binary data.
     * @param in input stream
     * @return {@linkplain Result read result}
     * @throws MalformedTagException if the tag data is malformed
     * @throws IOException if an I/O error occurs
     */
    public static @NotNull Result read(@NotNull InputStream in) throws IOException {
        return read((DataInput) new DataInputStream(in));
    }

    private static final class UncloseableInputStream extends FilterInputStream {
        public UncloseableInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() { }
    }

    /**
     * Deserializes an NBT structure from gzipped binary data.
     * @param in input stream
     * @return {@linkplain Result read result}
     * @throws MalformedTagException if the tag data is malformed
     * @throws IOException if an I/O error occurs
     */
    public static @NotNull Result readCompressed(@NotNull InputStream in) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(new UncloseableInputStream(in))) {
            return read(gis);
        }
    }

    /**
     * Serializes an NBT structure into binary data.
     * @param rootName root tag name
     * @param rootTag root tag
     * @param out output stream
     * @throws IOException if an I/O error occurs
     */
    public static void write(@NotNull String rootName, @NotNull CompoundTag rootTag, @NotNull OutputStream out) throws IOException {
        write(rootName, rootTag, (DataOutput) new DataOutputStream(out));
    }

    private static final class UncloseableOutputStream extends FilterOutputStream {
        public UncloseableOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() { }
    }

    /**
     * Serializes an NBT structure into gzipped binary data.
     * @param rootName root tag name
     * @param rootTag root tag
     * @param out output stream
     * @throws IOException if an I/O error occurs
     */
    public static void writeCompressed(@NotNull String rootName, @NotNull CompoundTag rootTag, @NotNull OutputStream out) throws IOException {
        try (GZIPOutputStream gos = new GZIPOutputStream(new UncloseableOutputStream(out))) {
            write(rootName, rootTag, gos);
        }
    }
}
