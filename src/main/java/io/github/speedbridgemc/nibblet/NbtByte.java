package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NbtNumber)
            return ((NbtNumber) obj).valueAsNumber().byteValue() == value;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.BYTE, value);
    }
}
