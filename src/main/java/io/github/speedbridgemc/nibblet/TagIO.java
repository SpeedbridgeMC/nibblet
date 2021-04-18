package io.github.speedbridgemc.nibblet;

import io.github.speedbridgemc.nibblet.stream.TagReader;
import io.github.speedbridgemc.nibblet.stream.TagStreamHandler;
import io.github.speedbridgemc.nibblet.stream.TagWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Provides methods to read and write NBT binaries.
 */
public final class TagIO {
    private TagIO() { }

    /**
     * Represents a named {@link RootTag}.
     * @param <T> tag type
     */
    public interface Named<T extends RootTag> {
        /**
         * Gets the tag itself.
         * @return tag
         */
        @NotNull T tag();

        /**
         * Gets the tag's name.
         * @return tag name
         */
        @NotNull String name();

        /**
         * Gets the tag's type.
         * @return tag type
         */
        @NotNull TagType tagType();
    }

    private static final class NamedCompoundTag implements Named<CompoundTag> {
        private final @NotNull CompoundTag tag;
        private final @NotNull String name;

        private NamedCompoundTag(@NotNull CompoundTag tag, @NotNull String name) {
            this.tag = tag;
            this.name = name;
        }

        @Override
        public @NotNull CompoundTag tag() {
            return tag;
        }

        @Override
        public @NotNull String name() {
            return name;
        }

        @Override
        public @NotNull TagType tagType() {
            return TagType.COMPOUND;
        }
    }

    private static final class NamedListTag implements Named<ListTag> {
        private final @NotNull ListTag tag;
        private final @NotNull String name;

        private NamedListTag(@NotNull ListTag tag, @NotNull String name) {
            this.tag = tag;
            this.name = name;
        }

        @Override
        public @NotNull ListTag tag() {
            return tag;
        }

        @Override
        public @NotNull String name() {
            return name;
        }

        @Override
        public @NotNull TagType tagType() {
            return TagType.LIST;
        }
    }

    /**
     * Reads an NBT structure from a stream.
     * @param streamHandler stream handler
     * @param in input stream
     * @return named tag that was read
     * @throws MalformedTagException if the tag data is malformed.
     * @throws IOException if an I/O error occurred.
     */
    public static @NotNull Named<? extends RootTag> read(@NotNull TagStreamHandler streamHandler, @NotNull InputStream in) throws IOException {
        try (TagReader reader = new TagReader(streamHandler, in)) {
            TagType rootType = reader.nextType();
            if (rootType == TagType.ROOT_LIST)
                return readRootList(reader);
            else if (rootType == TagType.COMPOUND)
                return readRootCompound(reader);
            else
                throw new MalformedTagException("Unsupported root tag type " + rootType);
        }
    }

    private static @NotNull NamedListTag readRootList(@NotNull TagReader reader) throws IOException {
        String rootName = reader.nextName();
        reader.beginRootList();
        ListTag listTag = ListTag.of(readTag(reader, reader.listItemType()));
        reader.endRootList();
        return new NamedListTag(listTag, rootName);
    }

    private static @NotNull NamedCompoundTag readRootCompound(@NotNull TagReader reader) throws IOException {
        reader.beginCompound();
        String rootName = reader.nextName();
        CompoundTag rootTag = readCompound(reader);
        reader.endCompound();
        return new NamedCompoundTag(rootTag, rootName);
    }

    private static @NotNull Tag readTag(@NotNull TagReader reader, @NotNull TagType type) throws IOException {
        switch (type) {
        case BYTE:
            return ByteTag.of(reader.nextByte());
        case SHORT:
            return ShortTag.of(reader.nextShort());
        case INT:
            return IntTag.of(reader.nextInt());
        case LONG:
            return LongTag.of(reader.nextLong());
        case FLOAT:
            return FloatTag.of(reader.nextFloat());
        case DOUBLE:
            return DoubleTag.of(reader.nextDouble());
        case BYTE_ARRAY:
            reader.beginByteArray();
            byte[] bArr = new byte[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                bArr[i] = reader.nextByte();
            reader.endByteArray();
            return ByteArrayTag.copyOf(bArr);
        case STRING:
            return StringTag.of(reader.nextString());
        case LIST:
            reader.beginList();
            ListTag.Builder lBuilder = ListTag.builder(reader.listSize());
            while (reader.listHasNext())
                lBuilder.add(readTag(reader, reader.listItemType()));
            reader.endList();
            return lBuilder.build();
        case COMPOUND:
            reader.beginCompound();
            CompoundTag cTag = readCompound(reader);
            reader.endCompound();
            return cTag;
        case INT_ARRAY:
            reader.beginIntArray();
            int[] iArr = new int[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                iArr[i] = reader.nextInt();
            reader.endIntArray();
            return IntArrayTag.copyOf(iArr);
        case LONG_ARRAY:
            reader.beginLongArray();
            long[] lArr = new long[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                lArr[i] = reader.nextLong();
            reader.endLongArray();
            return LongArrayTag.copyOf(lArr);
        default:
            throw new MalformedTagException("Unreadable tag type " + type);
        }
    }

    private static @NotNull CompoundTag readCompound(@NotNull TagReader reader) throws IOException {
        CompoundTag.Builder builder = CompoundTag.builder();
        TagType type = reader.nextType();
        while (type != TagType.END) {
            String name = reader.nextName();
            builder.put(name, readTag(reader, type));
            type = reader.nextType();
        }
        return builder.build();
    }

    /**
     * Writes an NBT structure to a stream.
     * @param rootName root tag name
     * @param rootTag root tag, either a {@link CompoundTag} or a {@link ListTag}
     * @param streamHandler stream handler
     * @param out output stream
     * @throws IOException if an I/O error occurs.
     */
    public static void write(@NotNull String rootName, @NotNull RootTag rootTag, @NotNull TagStreamHandler streamHandler, @NotNull OutputStream out)
            throws IOException {
        try (TagWriter writer = new TagWriter(streamHandler, out)) {
            writer.name(rootName);
            switch (rootTag.type()) {
            case COMPOUND:
                writeCompound(writer, (CompoundTagView) rootTag);
                break;
            case LIST:
                writeList(writer, (ListTagView) rootTag);
                break;
            default:
                throw new MalformedTagException("Unsupported root tag type " + rootTag.type());
            }
        }
    }

    private static void writeCompound(@NotNull TagWriter writer, @NotNull CompoundTagView tag) throws IOException {
        writer.beginCompound();
        for (Map.Entry<String, Tag> entry : tag.entries()) {
            writer.name(entry.getKey());
            writeTag(writer, entry.getValue());
        }
        writer.endCompound();
    }

    private static void writeList(@NotNull TagWriter writer, @NotNull ListTagView tag) throws IOException {
        writer.beginList();
        for (Tag item : tag)
            writeTag(writer, item);
        writer.endList();
    }

    private static void writeTag(@NotNull TagWriter writer, @NotNull Tag tag) throws IOException {
        switch (tag.type()) {
        case BYTE:
            writer.value(((ByteTag) tag).value());
            break;
        case SHORT:
            writer.value(((ShortTag) tag).value());
            break;
        case INT:
            writer.value(((IntTag) tag).value());
            break;
        case LONG:
            writer.value(((LongTag) tag).value());
            break;
        case FLOAT:
            writer.value(((FloatTag) tag).value());
            break;
        case DOUBLE:
            writer.value(((DoubleTag) tag).value());
            break;
        case BYTE_ARRAY:
            ByteArrayTagView baTag = (ByteArrayTagView) tag;
            writer.beginByteArray();
            for (int i = 0, size = baTag.length(); i < size; i++)
                writer.value(baTag.get(i));
            writer.endByteArray();
            break;
        case STRING:
            writer.value(((StringTag) tag).value());
            break;
        case LIST:
            writeList(writer, (ListTagView) tag);
            break;
        case COMPOUND:
            writeCompound(writer, (CompoundTagView) tag);
            break;
        case INT_ARRAY:
            IntArrayTagView iaTag = (IntArrayTagView) tag;
            writer.beginIntArray();
            for (int i = 0, size = iaTag.length(); i < size; i++)
                writer.value(iaTag.get(i));
            writer.endIntArray();
            break;
        case LONG_ARRAY:
            LongArrayTagView laTag = (LongArrayTagView) tag;
            writer.beginLongArray();
            for (int i = 0, size = laTag.length(); i < size; i++)
                writer.value(laTag.get(i));
            writer.endLongArray();
            break;
        default:
            throw new MalformedTagException("Unwritable tag type " + tag.type());
        }
    }
}
