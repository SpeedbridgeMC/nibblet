package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NbtLong implements NbtNumber {
    private final long value;

    private NbtLong(long value) {
        this.value = value;
    }

    public static @NotNull NbtLong of(long value) {
        return new NbtLong(value);
    }

    public long value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull NbtType type() {
        return NbtType.LONG;
    }

    @Override
    public @NotNull NbtLong copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NbtLong)
            return ((NbtLong) obj).value == value;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.LONG, value);
    }
}
