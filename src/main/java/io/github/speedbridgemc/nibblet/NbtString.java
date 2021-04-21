package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

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
}
