package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public class LongArrayTag implements Tag {
    protected final long[] backingArray;

    LongArrayTag(long[] backingArray) {
        this.backingArray = backingArray;
    }

    public static @NotNull LongArrayTag copyOf(long @NotNull ... values) {
        return new LongArrayTag(values.clone());
    }

    @Override
    public @NotNull TagType type() {
        return TagType.LONG_ARRAY;
    }

    public int length() {
        return backingArray.length;
    }

    public long get(int i) {
        return backingArray[i];
    }

    public long @NotNull [] toArray() {
        return backingArray.clone();
    }

    @Override
    public @NotNull LongArrayTag copy() {
        return this;
    }

    @Override
    public @NotNull MutableLongArrayTag mutableCopy() {
        return MutableLongArrayTag.copyOf(backingArray);
    }
}
