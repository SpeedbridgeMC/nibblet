package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

public interface IntArrayTagView extends Tag {
    @Override
    default @NotNull TagType type() {
        return TagType.INT_ARRAY;
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
    default @NotNull IntArrayTagView view() {
        return this;
    }

    @Override
    default @NotNull IntArrayTagView copy() {
        return this;
    }
}
