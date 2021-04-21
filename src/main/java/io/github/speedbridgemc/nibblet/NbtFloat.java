package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class NbtFloat implements NbtNumber {
    private final float value;

    private NbtFloat(float value) {
        this.value = value;
    }

    public static @NotNull NbtFloat of(float value) {
        return new NbtFloat(value);
    }

    public float value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull NbtType type() {
        return NbtType.FLOAT;
    }

    @Override
    public @NotNull NbtFloat copy() {
        return this;
    }
}
