package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class FloatTag implements NumberTag {
    private final float value;

    private FloatTag(float value) {
        this.value = value;
    }

    public static @NotNull FloatTag of(float value) {
        return new FloatTag(value);
    }

    public float value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.FLOAT;
    }

    @Override
    public @NotNull FloatTag copy() {
        return this;
    }
}
