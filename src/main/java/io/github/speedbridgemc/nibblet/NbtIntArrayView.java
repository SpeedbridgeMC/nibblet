package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface NbtIntArrayView extends NbtElement, Iterable<@NotNull Integer> {
    @ApiStatus.NonExtendable
    @Override
    default @NotNull NbtType type() {
        return NbtType.INT_ARRAY;
    }

    int length();
    int get(int i);
    @Override
    @NotNull Iterator<@NotNull Integer> iterator();

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
