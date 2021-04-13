package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class ShortTag implements NumberTag {
    private final short value;

    private ShortTag(short value) {
        this.value = value;
    }

    public static @NotNull ShortTag of(short value) {
        return new ShortTag(value);
    }

    public short value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.SHORT;
    }

    @Override
    public @NotNull ShortTag copy() {
        return this;
    }
}
