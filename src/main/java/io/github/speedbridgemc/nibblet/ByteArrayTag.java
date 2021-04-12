package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public class ByteArrayTag implements Tag {
    protected final byte[] backingArray;

    ByteArrayTag(byte[] backingArray) {
        this.backingArray = backingArray;
    }

    public static @NotNull ByteArrayTag copyOf(byte @NotNull ... values) {
        return new ByteArrayTag(values.clone());
    }

    @Override
    public @NotNull TagType type() {
        return TagType.BYTE_ARRAY;
    }

    public int length() {
        return backingArray.length;
    }

    public byte get(int i) {
        return backingArray[i];
    }

    public byte @NotNull [] toArray() {
        return backingArray.clone();
    }

    @Override
    public @NotNull ByteArrayTag copy() {
        return this;
    }

    @Override
    public @NotNull MutableByteArrayTag mutableCopy() {
        return MutableByteArrayTag.copyOf(backingArray);
    }
}
