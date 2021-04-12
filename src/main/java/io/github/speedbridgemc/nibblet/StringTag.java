package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class StringTag implements Tag {
    private final @NotNull String value;

    private StringTag(@NotNull String value) {
        this.value = value;
    }

    public static @NotNull StringTag of(@NotNull String value) {
        return new StringTag(value);
    }

    public @NotNull String value() {
        return value;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.STRING;
    }

    @Override
    public @NotNull StringTag copy() {
        return this;
    }
}
