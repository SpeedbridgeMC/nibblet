package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

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
}
