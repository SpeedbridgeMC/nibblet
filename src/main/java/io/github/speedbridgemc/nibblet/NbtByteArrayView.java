package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface NbtByteArrayView extends NbtElement, Iterable<@NotNull Byte> {
    @ApiStatus.NonExtendable
    @Override
    default @NotNull NbtType type() {
        return NbtType.BYTE_ARRAY;
    }

    int length();
    byte get(int i);
    @Override
    @NotNull Iterator<@NotNull Byte> iterator();

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
