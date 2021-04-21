package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public interface NbtIntArrayView extends NbtElement {
    @Override
    default @NotNull NbtType type() {
        return NbtType.INT_ARRAY;
    }

    int length();
    int get(int i);

    default int @NotNull [] toArray() {
        int[] array = new int[length()];
        for (int i = 0; i < array.length; i++)
            array[i] = get(i);
        return array;
    }

    @Override
    default @NotNull NbtIntArrayView view() {
        return this;
    }

    @Override
    default @NotNull NbtIntArrayView copy() {
        return this;
    }
}
