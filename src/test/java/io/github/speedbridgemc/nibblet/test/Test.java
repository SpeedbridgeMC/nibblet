package io.github.speedbridgemc.nibblet.test;

import io.github.speedbridgemc.nibblet.*;
import io.github.speedbridgemc.nibblet.stream.NbtWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
        try (OutputStream outUncompressed = Files.newOutputStream(pathS);
             GZIPOutputStream out = new GZIPOutputStream(outUncompressed);
             NbtWriter writer = new NbtWriter(NbtFormat.JAVA, out)) {
            writer.name("test_root")
                    .beginObject()
                    .name("test_nested")
                    .beginObject()
                    .name("hello").stringValue("world")
                    .endObject()
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
                    .endObject();
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
                    .beginObject()
                    .name("zero").intValue(0)
                    .name("negative_one").intValue(-1)
                    .name("positive_one").intValue(1)
                    .name("negative_two").longValue(-2)
                    .name("positive_two").longValue(2)
                    .endObject();
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + pathNet + "\"!");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(path)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.JAVA, in);
            System.out.println("Reading from file \"" + path + "\":");
            NbtStringifier.printWikiVGString(tag.name(), tag.element());
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + path + "\"");
            e.printStackTrace();
        }

        try (InputStream inCompressed = Files.newInputStream(pathS);
             GZIPInputStream in = new GZIPInputStream(inCompressed)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.JAVA, in);
            System.out.println("Reading from file \"" + pathS + "\":");
            NbtStringifier.printWikiVGString(tag.name(), tag.element());
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + pathS + "\"");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(pathSL)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.BEDROCK, in);
            System.out.println("Reading from file \"" + pathSL + "\":");
            NbtStringifier.printWikiVGString(tag.name(), tag.element());
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to read from \"" + pathSL + "\"");
            e.printStackTrace();
        }

        try (InputStream in = Files.newInputStream(pathNet)) {
            NbtIO.Named<?> tag = NbtIO.read(NbtFormat.BEDROCK_NETWORK, in);
            System.out.println("Reading from file \"" + pathNet + "\":");
            NbtStringifier.printWikiVGString(tag.name(), tag.element());
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
            NbtStringifier.printWikiVGString(tag.name(), tag.element());
        } catch (IOException e) {
            System.err.print("Failed to read from \"" + pathBig + "\"");
            e.printStackTrace();
        }
    }
}
