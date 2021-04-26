package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NbtDouble implements NbtNumber {
    private final double value;

    private NbtDouble(double value) {
        this.value = value;
    }

    public static @NotNull NbtDouble of(double value) {
        return new NbtDouble(value);
    }

    public double value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull NbtType type() {
        return NbtType.DOUBLE;
    }

    @Override
    public @NotNull NbtDouble copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NbtDouble)
            return ((NbtDouble) obj).value == value;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.DOUBLE, value);
    }
}
