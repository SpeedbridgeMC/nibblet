package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class IntTag implements NumberTag {
    private final int value;

    private IntTag(int value) {
        this.value = value;
    }

    public static @NotNull IntTag of(int value) {
        return new IntTag(value);
    }

    public int value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.INT;
    }

    @Override
    public @NotNull IntTag copy() {
        return this;
    }
}
