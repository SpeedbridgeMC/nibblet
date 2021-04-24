package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NbtInt implements NbtNumber {
    private final int value;

    private NbtInt(int value) {
        this.value = value;
    }

    public static @NotNull NbtInt of(int value) {
        return new NbtInt(value);
    }

    public int value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull NbtType type() {
        return NbtType.INT;
    }

    @Override
    public @NotNull NbtInt copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NbtNumber)
            return ((NbtNumber) obj).valueAsNumber().intValue() == value;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.INT, value);
    }
}
