package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class LongTag implements NumberTag {
    private final long value;

    private LongTag(long value) {
        this.value = value;
    }

    public static @NotNull LongTag of(long value) {
        return new LongTag(value);
    }

    public long value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.LONG;
    }

    @Override
    public @NotNull LongTag copy() {
        return this;
    }
}
