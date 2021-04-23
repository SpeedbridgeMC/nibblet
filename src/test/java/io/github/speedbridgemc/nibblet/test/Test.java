package io.github.speedbridgemc.nibblet.test;

import io.github.speedbridgemc.nibblet.*;
import io.github.speedbridgemc.nibblet.stream.NbtWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class Test {
    public static void main(String[] args) {
        Path path = Paths.get(".", "test.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(path)) {
            NbtIO.write("test_root", NbtObject.builder()
                    .put("test_nested", NbtObject.builder()
                            .putString("hello", "world")
                            .build())
                    .putInt("test_int", 24)
                    .put("test_list", NbtList.builder()
                            .add(NbtString.of("a"))
                            .add(NbtString.of("b"))
                            .add(NbtString.of("c"))
                            .build())
                    .putIntArray("test_int_array", 1, 2, 3, 4, 5)
                    .put("test_nested_list", NbtList.builder()
                            .add(NbtList.of(NbtInt.of(1)))
                            .add(NbtList.of(NbtDouble.of(2)))
                            .add(NbtList.of(NbtString.of("3")))
                            .build())
                    .build(), NbtFormat.JAVA, out);
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + path + "\"!");
            e.printStackTrace();
        }

        Path pathS = Paths.get(".", "test_stream.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(pathS);
             NbtWriter writer = new NbtWriter(NbtFormat.BEDROCK, out)) {
            writer.name("test_root")
                    .beginCompound()
                    .name("test_nested")
                    .beginCompound()
                    .name("hello").stringValue("world")
                    .endCompound()
                    .name("test_int")
                    .intValue(24)
                    .name("test_list")
                    .beginList()
                    .stringValue("a")
                    .stringValue("b")
                    .stringValue("c")
                    .endList()
                    .name("test_int_array")
                    .beginIntArray()
                    .intValues(1, 2, 3)
                    .intValue(4)
                    .intValue(5)
                    .endIntArray()
                    .name("test_nested_list")
                    .beginList()
                    .beginList()
                    .intValue(1)
                    .endList()
                    .beginList()
                    .doubleValue(2d)
                    .endList()
                    .beginList()
                    .stringValue("3")
                    .endList()
                    .endList()
                    .endCompound();
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + pathS + "\"!");
            e.printStackTrace();
        }

        Path pathSL = Paths.get(".", "test_stream_list.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(pathSL);
             NbtWriter writer = new NbtWriter(NbtFormat.BEDROCK, out)) {
            writer.name("root_list")
                    .beginList()
                    .stringValue("a")
                    .stringValue("b")
                    .stringValue("c")
                    .endList();
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + pathSL + "\"!");
            e.printStackTrace();
        }

        Path pathNet = Paths.get(".", "test_network.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(pathNet);
             NbtWriter writer = new NbtWriter(NbtFormat.BEDROCK_NETWORK, out)) {
            writer.name("net_test")
                    .beginCompound()
                    .name("zero").intValue(0)
                    .name("negative_one").intValue(-1)
                    .name("positive_one").intValue(1)
                    .name("negative_two").longValue(-2)
                    .name("positive_two").longValue(2)
                    .endCompound();
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + pathNet + "\"!");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(path)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.JAVA, in);
            System.out.println("Reading from file \"" + path + "\":");
            printTag(tag.element(), tag.name(), "");
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + path + "\"");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(pathS)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.BEDROCK, in);
            System.out.println("Reading from file \"" + pathS + "\":");
            printTag(tag.element(), tag.name(), "");
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + pathS + "\"");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(pathSL)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.BEDROCK, in);
            System.out.println("Reading from file \"" + pathSL + "\":");
            printTag(tag.element(), tag.name(), "");
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + pathSL + "\"");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(pathNet)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.BEDROCK_NETWORK, in);
            System.out.println("Reading from file \"" + pathNet + "\":");
            printTag(tag.element(), tag.name(), "");
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + pathNet + "\"");
            e.printStackTrace();
        }

        // get bigtest.nbt from https://raw.github.com/Dav1dde/nbd/master/test/bigtest.nbt
        Path pathBig = Paths.get(".", "bigtest.nbt").toAbsolutePath().normalize();
        try (InputStream inCompressed = Files.newInputStream(pathBig);
             GZIPInputStream in = new GZIPInputStream(inCompressed)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.JAVA, in);
            System.out.println("Reading from file \"" + pathBig + "\":");
            printTag(tag.element(), tag.name(), "");
        } catch (IOException e) {
            System.err.print("Failed to read from \"" + pathBig + "\"");
            e.printStackTrace();
        }
    }

    private static void printTag(@NotNull NbtElement nbt, @NotNull String name, @NotNull String indent) {
        System.out.format("%s%s(%s): ", indent, nbt.type(), name.isEmpty() ? "None" : "'" + name + "'");
        switch (nbt.type()) {
        case BYTE:
            System.out.format("%d%n", ((NbtByte) nbt).value());
            break;
        case SHORT:
            System.out.format("%d%n", ((NbtShort) nbt).value());
            break;
        case INT:
            System.out.format("%d%n", ((NbtInt) nbt).value());
            break;
        case LONG:
            System.out.format("%d%n", ((NbtLong) nbt).value());
            break;
        case FLOAT:
            System.out.format("%g%n", ((NbtFloat) nbt).value());
            break;
        case DOUBLE:
            System.out.format("%g%n", ((NbtDouble) nbt).value());
            break;
        case BYTE_ARRAY:
            NbtByteArrayView nbtByteArr = (NbtByteArrayView) nbt;
            System.out.print("[");
            for (int i = 0, length = nbtByteArr.length(); i < length; i++) {
                System.out.format("%d", nbtByteArr.get(i));
                if (i < length - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
            break;
        case STRING:
            System.out.format("'%s'%n", ((NbtString) nbt).value());
            break;
        case LIST:
            NbtListView nbtList = (NbtListView) nbt;
            System.out.format("%s%n%s{%n", entryCount(nbtList.size()), indent);
            for (NbtElement item : nbtList)
                printTag(item, "", indent + "  ");
            System.out.format("%s}%n", indent);
            break;
        case COMPOUND:
            NbtObjectView nbtObj = (NbtObjectView) nbt;
            System.out.format("%s%n%s{%n", entryCount(nbtObj.size()), indent);
            for (Map.Entry<String, NbtElement> entry : nbtObj.entries())
                printTag(entry.getValue(), entry.getKey(), indent + "  ");
            System.out.format("%s}%n", indent);
            break;
        case INT_ARRAY:
            NbtIntArrayView nbtIntArr = (NbtIntArrayView) nbt;
            System.out.print("[");
            for (int i = 0, length = nbtIntArr.length(); i < length; i++) {
                System.out.format("%d", nbtIntArr.get(i));
                if (i < length - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
            break;
        case LONG_ARRAY:
            NbtLongArrayView nbtLongTag = (NbtLongArrayView) nbt;
            System.out.print("[");
            for (int i = 0, length = nbtLongTag.length(); i < length; i++) {
                System.out.format("%d", nbtLongTag.get(i));
                if (i < length - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
            break;
        default:
            throw new RuntimeException("Unprintable tag type " + nbt.type());
        }
    }

    private static @NotNull String entryCount(int entries) {
        return entries == 1 ? "1 entry" : entries + " entries";
    }
}
