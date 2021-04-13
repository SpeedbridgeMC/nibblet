package io.github.speedbridgemc.nibblet.test;

import io.github.speedbridgemc.nibblet.*;
import io.github.speedbridgemc.nibblet.TagFormats;
import io.github.speedbridgemc.nibblet.stream.TagWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                    .build(), out);
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
            writer.beginList()
                    .value("a")
                    .value("b")
                    .value("c")
                    .endList();
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + pathSL + "\"!");
            e.printStackTrace();
        }
    }
}
