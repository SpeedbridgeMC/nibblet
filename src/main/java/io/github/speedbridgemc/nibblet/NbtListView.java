package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface NbtListView extends NbtRootElement, Iterable<NbtElement> {
    @ApiStatus.NonExtendable
    @Override
    default @NotNull NbtType type() {
        return NbtType.LIST;
    }

    @NotNull NbtType itemType();
    int size();
    @NotNull NbtElement get(int i);
    @Override
    @NotNull Iterator<@NotNull NbtElement> iterator();

    default byte getByte(int i, byte defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().byteValue();
        return defaultValue;
    }

    default boolean getBoolean(int i, boolean defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().byteValue() > 0;
        return defaultValue;
    }

    default short getShort(int i, short defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().shortValue();
        return defaultValue;
    }

    default int getInt(int i, int defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().intValue();
        return defaultValue;
    }

    default long getLong(int i, long defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().longValue();
        return defaultValue;
    }

    default float getFloat(int i, float defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().floatValue();
        return defaultValue;
    }

    default double getDouble(int i, double defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().doubleValue();
        return defaultValue;
    }

    default byte @NotNull [] getByteArray(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtByteArrayView)
            return ((NbtByteArrayView) nbt).toArray();
        return EMPTY_BYTE_ARRAY;
    }

    default @NotNull String getString(int i, @NotNull String defaultValue) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtString)
            return ((NbtString) nbt).value();
        return defaultValue;
    }

    default @Nullable NbtListView getList(int i, @NotNull NbtType itemType) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtListView) {
            NbtListView listTag = ((NbtListView) nbt).view();
            if (listTag.itemType() == itemType)
                return listTag;
        }
        return null;
    }

    default @Nullable NbtObjectView getCompound(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtObjectView)
            return ((NbtObjectView) nbt).view();
        return null;
    }

    default int @NotNull [] getIntArray(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtIntArrayView)
            return ((NbtIntArrayView) nbt).toArray();
        return EMPTY_INT_ARRAY;
    }

    default long @NotNull [] getLongArray(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtLongArrayView)
            return ((NbtLongArrayView) nbt).toArray();
        return EMPTY_LONG_ARRAY;
    }

    @Override
    default @NotNull NbtListView view() {
        return this;
    }

    @Override
    default @NotNull NbtListView copy() {
        return this;
    }
}
