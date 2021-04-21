package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class NbtByte implements NbtNumber {
    private final byte value;

    private NbtByte(byte value) {
        this.value = value;
    }

    public static @NotNull NbtByte of(byte value) {
        return new NbtByte(value);
    }

    public byte value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull NbtType type() {
        return NbtType.BYTE;
    }

    @Override
    public @NotNull NbtByte copy() {
        return this;
    }
}
