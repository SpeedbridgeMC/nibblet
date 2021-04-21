package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

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
}
