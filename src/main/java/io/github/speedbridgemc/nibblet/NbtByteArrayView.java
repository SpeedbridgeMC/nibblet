package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public interface NbtByteArrayView extends NbtElement {
    @Override
    default @NotNull NbtType type() {
        return NbtType.BYTE_ARRAY;
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
    default @NotNull NbtByteArrayView view() {
        return this;
    }

    @Override
    default @NotNull NbtByteArrayView copy() {
        return this;
    }
}
