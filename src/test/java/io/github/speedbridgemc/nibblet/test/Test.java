package io.github.speedbridgemc.nibblet.test;

import io.github.speedbridgemc.nibblet.*;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Test {
    public static void main(String[] args) {
        CompoundTag tag = CompoundTag.builder()
                .putByte("test_byte", (byte) 2)
                .putShort("test_short", (short) 24)
                .putInt("test_int", 246)
                .putLong("test_long", 2468)
                .putFloat("test_float", 24.6f)
                .putDouble("test_double", 24.68)
                .putByteArray("test_byte_array", new byte[] { 2, 4, 6, 8 })
                .putString("test_string", "hello world")
                .putIntArray("test_int_array", 24, 68)
                .putLongArray("test_long_array", 246, 810)
                .put("test_list", ListTag.builder()
                        .add(StringTag.of("hello"))
                        .add(StringTag.of("world"))
                        .build())
                .put("test_compound", CompoundTag.builder()
                        .putBoolean("is_this_cool", true)
                        .build())
                .build();

        Path path = Paths.get(".", "test.nbt").toAbsolutePath().normalize();
        try (OutputStream out = Files.newOutputStream(path)) {
            TagIO.write("test_root", tag, out);
        } catch (IOException e) {
            System.err.println("Failed to write to \"" + path + "\"!");
            e.printStackTrace();
        }
    }
}
