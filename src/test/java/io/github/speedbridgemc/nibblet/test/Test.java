package io.github.speedbridgemc.nibblet.test;

import io.github.speedbridgemc.nibblet.*;
import io.github.speedbridgemc.nibblet.TagFormats;
import io.github.speedbridgemc.nibblet.stream.TagWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public final class Test {
    public static void main(String[] args) {
        Path path = Paths.get(".", "test.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(path)) {
            TagIO.write("test_root", CompoundTag.builder()
                    .put("test_nested", CompoundTag.builder()
                            .putString("hello", "world")
                            .build())
                    .putInt("test_int", 24)
                    .put("test_list", ListTag.builder()
                            .add(StringTag.of("a"))
                            .add(StringTag.of("b"))
                            .add(StringTag.of("c"))
                            .build())
                    .putIntArray("test_int_array", 1, 2, 3, 4, 5)
                    .put("test_nested_list", ListTag.builder()
                            .add(ListTag.of(IntTag.of(1)))
                            .add(ListTag.of(DoubleTag.of(2)))
                            .add(ListTag.of(StringTag.of("3")))
                            .build())
                    .build(), TagFormats.JAVA, out);
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + path + "\"!");
            e.printStackTrace();
        }

        Path pathS = Paths.get(".", "test_stream.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(pathS);
             TagWriter writer = new TagWriter(TagFormats.JAVA, out)) {
            writer.name("test_root")
                    .beginCompound()
                    .name("test_nested")
                    .beginCompound()
                    .name("hello").value("world")
                    .endCompound()
                    .name("test_int")
                    .value(24)
                    .name("test_list")
                    .beginList()
                    .value("a")
                    .value("b")
                    .value("c")
                    .endList()
                    .name("test_int_array")
                    .beginIntArray()
                    .values(1, 2, 3)
                    .value(4)
                    .value(5)
                    .endIntArray()
                    .name("test_nested_list")
                    .beginList()
                    .beginList()
                    .value(1)
                    .endList()
                    .beginList()
                    .value(2d)
                    .endList()
                    .beginList()
                    .value("3")
                    .endList()
                    .endList()
                    .endCompound();
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + pathS + "\"!");
            e.printStackTrace();
        }

        Path pathSL = Paths.get(".", "test_stream_list.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(pathSL);
             TagWriter writer = new TagWriter(TagFormats.BEDROCK, out)) {
            writer.name("")
                    .beginList()
                    .value("a")
                    .value("b")
                    .value("c")
                    .endList();
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + pathSL + "\"!");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(path)) {
            TagIO.Named<?> tag = TagIO.read(TagFormats.JAVA, in);
            if (tag.tagType() != TagType.COMPOUND)
                throw new IOException("Unsupported root tag type " + tag.tagType());
            printTag(tag.tag(), tag.name(), "");
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + path + "\"");
            e.printStackTrace();
        }
    }

    private static void printTag(@NotNull Tag tag, @Nullable String name, @NotNull String indent) {
        System.out.format("%s%s(%s): ", indent, tag.type(), name == null ? "None" : "'" + name + "'");
        switch (tag.type()) {
        case BYTE:
            System.out.format("%d%n", ((ByteTag) tag).value());
            break;
        case SHORT:
            System.out.format("%d%n", ((ShortTag) tag).value());
            break;
        case INT:
            System.out.format("%d%n", ((IntTag) tag).value());
            break;
        case LONG:
            System.out.format("%d%n", ((LongTag) tag).value());
            break;
        case FLOAT:
            System.out.format("%g%n", ((FloatTag) tag).value());
            break;
        case DOUBLE:
            System.out.format("%g%n", ((DoubleTag) tag).value());
            break;
        case BYTE_ARRAY:
            ByteArrayTagView baTag = (ByteArrayTagView) tag;
            System.out.print("[");
            for (int i = 0, length = baTag.length(); i < length; i++) {
                System.out.format("%d", baTag.get(i));
                if (i < length - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
            break;
        case STRING:
            System.out.format("'%s'%n", ((StringTag) tag).value());
            break;
        case LIST:
            ListTagView listTag = (ListTagView) tag;
            System.out.format("%s%n%s{%n", entryCount(listTag.size()), indent);
            for (Tag item : listTag)
                printTag(item, null, indent + "  ");
            System.out.format("%s}%n", indent);
            break;
        case COMPOUND:
            CompoundTagView compoundTag = (CompoundTagView) tag;
            System.out.format("%s%n%s{%n", entryCount(compoundTag.size()), indent);
            for (Map.Entry<String, Tag> entry : compoundTag.entries())
                printTag(entry.getValue(), entry.getKey(), indent + "  ");
            System.out.format("%s}%n", indent);
            break;
        case INT_ARRAY:
            IntArrayTagView iaTag = (IntArrayTagView) tag;
            System.out.print("[");
            for (int i = 0, length = iaTag.length(); i < length; i++) {
                System.out.format("%d", iaTag.get(i));
                if (i < length - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
            break;
        case LONG_ARRAY:
            LongArrayTagView laTag = (LongArrayTagView) tag;
            System.out.print("[");
            for (int i = 0, length = laTag.length(); i < length; i++) {
                System.out.format("%d", laTag.get(i));
                if (i < length - 1)
                    System.out.print(", ");
            }
            System.out.println("]");
            break;
        default:
            throw new RuntimeException("Unprintable tag type " + tag.type());
        }
    }

    private static @NotNull String entryCount(int entries) {
        return entries == 1 ? "1 entry" : entries + " entries";
    }
}
