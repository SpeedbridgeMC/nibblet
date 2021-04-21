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

    private static final class NamedNbtObject implements Named<NbtObject> {
        private final @NotNull NbtObject element;
        private final @NotNull String name;

        private NamedNbtObject(@NotNull NbtObject element, @NotNull String name) {
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

    private static final class NamedNbtList implements Named<NbtList> {
        private final @NotNull NbtList element;
        private final @NotNull String name;

        private NamedNbtList(@NotNull NbtList element, @NotNull String name) {
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

    private static @NotNull NbtIO.NamedNbtList readRootList(@NotNull NbtReader reader) throws IOException {
        String rootName = reader.nextName();
        reader.beginRootList();
        NbtList listElem = NbtList.of(readElement(reader, reader.listItemType()));
        reader.endRootList();
        return new NamedNbtList(listElem, rootName);
    }

    private static @NotNull NbtIO.NamedNbtObject readRootCompound(@NotNull NbtReader reader) throws IOException {
        reader.beginCompound();
        String rootName = reader.nextName();
        NbtObject objElem = readCompound(reader);
        reader.endCompound();
        return new NamedNbtObject(objElem, rootName);
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
            byte[] byteArr = new byte[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                byteArr[i] = reader.nextByte();
            reader.endByteArray();
            return NbtByteArray.copyOf(byteArr);
        case STRING:
            return NbtString.of(reader.nextString());
        case LIST:
            reader.beginList();
            NbtList.Builder listBuilder = NbtList.builder(reader.listSize());
            while (reader.listHasNext())
                listBuilder.add(readElement(reader, reader.listItemType()));
            reader.endList();
            return listBuilder.build();
        case COMPOUND:
            reader.beginCompound();
            NbtObject objElem = readCompound(reader);
            reader.endCompound();
            return objElem;
        case INT_ARRAY:
            reader.beginIntArray();
            int[] intArr = new int[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                intArr[i] = reader.nextInt();
            reader.endIntArray();
            return NbtIntArray.copyOf(intArr);
        case LONG_ARRAY:
            reader.beginLongArray();
            long[] longArr = new long[reader.listSize()];
            for (int i = 0; reader.listHasNext(); i++)
                longArr[i] = reader.nextLong();
            reader.endLongArray();
            return NbtLongArray.copyOf(longArr);
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
            writer.byteValue(((NbtByte) element).value());
            break;
        case SHORT:
            writer.shortValue(((NbtShort) element).value());
            break;
        case INT:
            writer.intValue(((NbtInt) element).value());
            break;
        case LONG:
            writer.longValue(((NbtLong) element).value());
            break;
        case FLOAT:
            writer.floatValue(((NbtFloat) element).value());
            break;
        case DOUBLE:
            writer.doubleValue(((NbtDouble) element).value());
            break;
        case BYTE_ARRAY:
            NbtByteArrayView byteArrElem = (NbtByteArrayView) element;
            writer.beginByteArray();
            for (int i = 0, size = byteArrElem.length(); i < size; i++)
                writer.byteValue(byteArrElem.get(i));
            writer.endByteArray();
            break;
        case STRING:
            writer.stringValue(((NbtString) element).value());
            break;
        case LIST:
            writeList(writer, (NbtListView) element);
            break;
        case COMPOUND:
            writeCompound(writer, (NbtObjectView) element);
            break;
        case INT_ARRAY:
            NbtIntArrayView intArrElem = (NbtIntArrayView) element;
            writer.beginIntArray();
            for (int i = 0, size = intArrElem.length(); i < size; i++)
                writer.intValue(intArrElem.get(i));
            writer.endIntArray();
            break;
        case LONG_ARRAY:
            NbtLongArrayView longArrElem = (NbtLongArrayView) element;
            writer.beginLongArray();
            for (int i = 0, size = longArrElem.length(); i < size; i++)
                writer.longValue(longArrElem.get(i));
            writer.endLongArray();
            break;
        default:
            throw new MalformedNbtException("Unwritable element type " + element.type());
        }
    }
}
