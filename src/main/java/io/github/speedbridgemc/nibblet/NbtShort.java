package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NbtShort implements NbtNumber {
    private final short value;

    private NbtShort(short value) {
        this.value = value;
    }

    public static @NotNull NbtShort of(short value) {
        return new NbtShort(value);
    }

    public short value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull NbtType type() {
        return NbtType.SHORT;
    }

    @Override
    public @NotNull NbtShort copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NbtShort)
            return ((NbtShort) obj).value == value;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.SHORT, value);
    }
}
