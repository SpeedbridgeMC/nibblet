package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public interface ByteArrayTagView extends Tag {
    @Override
    default @NotNull TagType type() {
        return TagType.BYTE_ARRAY;
    }

    int length();
    byte get(int i);

    default byte @NotNull [] toArray() {
        byte[] array = new byte[length()];
        for (int i = 0; i < array.length; i++)
            array[i] = get(i);
        return array;
    }

    @Override
    default @NotNull ByteArrayTagView view() {
        return this;
    }

    @Override
    default @NotNull ByteArrayTagView copy() {
        return this;
    }
}
