package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NbtString implements NbtElement {
    private final @NotNull String value;

    private NbtString(@NotNull String value) {
        this.value = value;
    }

    public static @NotNull NbtString of(@NotNull String value) {
        return new NbtString(value);
    }

    public @NotNull String value() {
        return value;
    }

    @Override
    public @NotNull NbtType type() {
        return NbtType.STRING;
    }

    @Override
    public @NotNull NbtString copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NbtString nbtString = (NbtString) o;
        return value.equals(nbtString.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.STRING, value);
    }
}
