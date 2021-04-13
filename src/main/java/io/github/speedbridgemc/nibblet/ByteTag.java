package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public final class ByteTag implements NumberTag {
    private final byte value;

    private ByteTag(byte value) {
        this.value = value;
    }

    public static @NotNull ByteTag of(byte value) {
        return new ByteTag(value);
    }

    public byte value() {
        return value;
    }

    @Override
    public @NotNull Number valueAsNumber() {
        return value;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.BYTE;
    }

    @Override
    public @NotNull ByteTag copy() {
        return this;
    }
}
