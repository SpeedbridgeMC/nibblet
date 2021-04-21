package io.github.speedbridgemc.nibblet;

import io.github.speedbridgemc.nibblet.stream.NbtReader;
import io.github.speedbridgemc.nibblet.stream.NbtStreamHandler;
import io.github.speedbridgemc.nibblet.stream.NbtWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Provides methods to read and write NBT binaries.
 */
public final class NbtIO {
    private NbtIO() { }

    /**
     * Represents a named {@link NbtRootElement}.
     * @param <T> NBT element type
     */
    public interface Named<T extends NbtRootElement> {
        /**
         * Gets the NBT element itself.
         * @return element
         */
        @NotNull T element();

        /**
         * Gets the NBT element's name.
         * @return element name
         */
        @NotNull String name();

        /**
         * Gets the NBT element's type.
         * @return element type
         */
        @NotNull NbtType elementType();
    }

    private static final class NamedCompoundelement implements Named<NbtObject> {
        private final @NotNull NbtObject element;
        private final @NotNull String name;

        private NamedCompoundelement(@NotNull NbtObject element, @NotNull String name) {
            this.element = element;
            this.name = name;
        }

        @Override
        public @NotNull NbtObject element() {
            return element;
        }

        @Override
        public @NotNull String name() {
            return name;
        }

        @Override
        public @NotNull NbtType elementType() {
            return NbtType.COMPOUND;
        }
    }

    private static final class NamedListelement implements Named<NbtList> {
        private final @NotNull NbtList element;
        private final @NotNull String name;

        private NamedListelement(@NotNull NbtList element, @NotNull String name) {
            this.element = element;
            this.name = name;
        }

        @Override
        public @NotNull NbtList element() {
            return element;
        }

        @Override
        public @NotNull String name() {
            return name;
        }

        @Override
        public @NotNull NbtType elementType() {
            return NbtType.LIST;
        }
    }

    /**
     * Reads an NBT structure from a stream.
     * @param streamHandler stream handler
     * @param in input stream
     * @return named element that was read
     * @throws MalformedNbtException if the element data is malformed.
     * @throws IOException if an I/O error occurred.
     */
    public static @NotNull Named<? extends NbtRootElement> read(@NotNull NbtStreamHandler streamHandler, @NotNull InputStream in) throws IOException {
        try (NbtReader reader = new NbtReader(streamHandler, in)) {
            NbtType rootType = reader.nextType();
            if (rootType == NbtType.ROOT_LIST)
                return readRootList(reader);
            else if (rootType == NbtType.COMPOUND)
                return readRootCompound(reader);
            else
                throw new MalformedNbtException("Unsupported root element type " + rootType);
        }
    }

    private static @NotNull NamedListelement readRootList(@NotNull NbtReader reader) throws IOException {
        String rootName = reader.nextName();
        reader.beginRootList();
        NbtList listelement = NbtList.of(readElement(reader, reader.listItemType()));
        reader.endRootList();
        return new NamedListelement(listelement, rootName);
    }

    private static @NotNull NamedCompoundelement readRootCompound(@NotNull NbtReader reader) throws IOException {
        reader.beginCompound();
        String rootName = reader.nextName();
        NbtObject rootelement = readCompound(reader);
        reader.endCompound();
        return new NamedCompoundelement(rootelement, rootName);
    }

    private static @NotNull NbtElement readElement(@NotNull NbtReader reader, @NotNull NbtType type) throws IOException {
        switch (type) {
        case BYTE:
            return NbtByte.of(reader.nextByte());
        case SHORT:
            return NbtShort.of(reader.nextShort());
        case INT:
            return NbtInt.of(reader.nextInt());
        case LONG:
            return NbtLong.of(reader.nextLong());
        case FLOAT:
            return NbtFloat.of(reader.nextFloat());
        case DOUBLE:
            return NbtDouble.of(reader.nextDouble());
        case BYTE_ARRAY:
            reader.beginByteArray();
            byte[] bArr = new byte[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                bArr[i] = reader.nextByte();
            reader.endByteArray();
            return NbtByteArray.copyOf(bArr);
        case STRING:
            return NbtString.of(reader.nextString());
        case LIST:
            reader.beginList();
            NbtList.Builder lBuilder = NbtList.builder(reader.listSize());
            while (reader.listHasNext())
                lBuilder.add(readElement(reader, reader.listItemType()));
            reader.endList();
            return lBuilder.build();
        case COMPOUND:
            reader.beginCompound();
            NbtObject celement = readCompound(reader);
            reader.endCompound();
            return celement;
        case INT_ARRAY:
            reader.beginIntArray();
            int[] iArr = new int[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                iArr[i] = reader.nextInt();
            reader.endIntArray();
            return NbtIntArray.copyOf(iArr);
        case LONG_ARRAY:
            reader.beginLongArray();
            long[] lArr = new long[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                lArr[i] = reader.nextLong();
            reader.endLongArray();
            return NbtLongArray.copyOf(lArr);
        default:
            throw new MalformedNbtException("Unreadable element type " + type);
        }
    }

    private static @NotNull NbtObject readCompound(@NotNull NbtReader reader) throws IOException {
        NbtObject.Builder builder = NbtObject.builder();
        NbtType type = reader.nextType();
        while (type != NbtType.END) {
            String name = reader.nextName();
            builder.put(name, readElement(reader, type));
            type = reader.nextType();
        }
        return builder.build();
    }

    /**
     * Writes an NBT structure to a stream.
     * @param rootName root element name
     * @param rootElement root element, either a {@link NbtObject} or a {@link NbtList}
     * @param streamHandler stream handler
     * @param out output stream
     * @throws IOException if an I/O error occurs.
     */
    public static void write(@NotNull String rootName, @NotNull NbtRootElement rootElement, @NotNull NbtStreamHandler streamHandler, @NotNull OutputStream out)
            throws IOException {
        try (NbtWriter writer = new NbtWriter(streamHandler, out)) {
            writer.name(rootName);
            switch (rootElement.type()) {
            case COMPOUND:
                writeCompound(writer, (NbtObjectView) rootElement);
                break;
            case LIST:
                writeList(writer, (NbtListView) rootElement);
                break;
            default:
                throw new MalformedNbtException("Unsupported root element type " + rootElement.type());
            }
        }
    }

    private static void writeCompound(@NotNull NbtWriter writer, @NotNull NbtObjectView element) throws IOException {
        writer.beginCompound();
        for (Map.Entry<String, NbtElement> entry : element.entries()) {
            writer.name(entry.getKey());
            writeElement(writer, entry.getValue());
        }
        writer.endCompound();
    }

    private static void writeList(@NotNull NbtWriter writer, @NotNull NbtListView element) throws IOException {
        writer.beginList();
        for (NbtElement item : element)
            writeElement(writer, item);
        writer.endList();
    }

    private static void writeElement(@NotNull NbtWriter writer, @NotNull NbtElement element) throws IOException {
        switch (element.type()) {
        case BYTE:
            writer.value(((NbtByte) element).value());
            break;
        case SHORT:
            writer.value(((NbtShort) element).value());
            break;
        case INT:
            writer.value(((NbtInt) element).value());
            break;
        case LONG:
            writer.value(((NbtLong) element).value());
            break;
        case FLOAT:
            writer.value(((NbtFloat) element).value());
            break;
        case DOUBLE:
            writer.value(((NbtDouble) element).value());
            break;
        case BYTE_ARRAY:
            NbtByteArrayView baelement = (NbtByteArrayView) element;
            writer.beginByteArray();
            for (int i = 0, size = baelement.length(); i < size; i++)
                writer.value(baelement.get(i));
            writer.endByteArray();
            break;
        case STRING:
            writer.value(((NbtString) element).value());
            break;
        case LIST:
            writeList(writer, (NbtListView) element);
            break;
        case COMPOUND:
            writeCompound(writer, (NbtObjectView) element);
            break;
        case INT_ARRAY:
            NbtIntArrayView iaelement = (NbtIntArrayView) element;
            writer.beginIntArray();
            for (int i = 0, size = iaelement.length(); i < size; i++)
                writer.value(iaelement.get(i));
            writer.endIntArray();
            break;
        case LONG_ARRAY:
            NbtLongArrayView laelement = (NbtLongArrayView) element;
            writer.beginLongArray();
            for (int i = 0, size = laelement.length(); i < size; i++)
                writer.value(laelement.get(i));
            writer.endLongArray();
            break;
        default:
            throw new MalformedNbtException("Unwritable element type " + element.type());
        }
    }
}
