package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class DoubleTag implements NumberTag {
    private final double value;

    private DoubleTag(double value) {
        this.value = value;
    }

    public static @NotNull DoubleTag of(double value) {
        return new DoubleTag(value);
    }

    public double value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.DOUBLE;
    }

    @Override
    public @NotNull DoubleTag copy() {
        return this;
    }
}
