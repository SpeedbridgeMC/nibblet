package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public class IntArrayTag implements Tag {
    protected final int[] backingArray;

    IntArrayTag(int[] backingArray) {
        this.backingArray = backingArray;
    }

    public static @NotNull IntArrayTag copyOf(int @NotNull ... values) {
        return new IntArrayTag(values.clone());
    }

    @Override
    public @NotNull TagType type() {
        return TagType.INT_ARRAY;
    }

    public int length() {
        return backingArray.length;
    }

    public int get(int i) {
        return backingArray[i];
    }

    public int @NotNull [] toArray() {
        return backingArray.clone();
    }

    @Override
    public @NotNull IntArrayTag copy() {
        return this;
    }

    @Override
    public @NotNull MutableIntArrayTag mutableCopy() {
        return MutableIntArrayTag.copyOf(backingArray);
    }
}
