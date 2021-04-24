package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface NbtLongArrayView extends NbtElement, Iterable<@NotNull Long> {
    @Override
    default @NotNull NbtType type() {
        return NbtType.LONG_ARRAY;
    }

    int length();
    long get(int i);
    @Override
    @NotNull Iterator<@NotNull Long> iterator();

    default long @NotNull [] toArray() {
        long[] array = new long[length()];
        for (int i = 0; i < array.length; i++)
            array[i] = get(i);
        return array;
    }

    @Override
    default @NotNull NbtLongArrayView view() {
        return this;
    }

    @Override
    default @NotNull NbtLongArrayView copy() {
        return this;
    }
}
