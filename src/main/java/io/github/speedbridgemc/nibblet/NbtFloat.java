package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NbtFloat)
            return ((NbtFloat) obj).value == value;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.FLOAT, value);
    }
}
