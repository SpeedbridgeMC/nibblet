package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;

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

    default @NotNull Optional<byte[]> getByteArray(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtByteArrayView)
            return Optional.of(((NbtByteArrayView) nbt).toArray());
        return Optional.empty();
    }

    default @NotNull Optional<String> getString(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtString)
            return Optional.of(((NbtString) nbt).value());
        return Optional.empty();
    }

    default @NotNull Optional<NbtListView> getList(int i, @NotNull NbtType itemType) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtListView) {
            NbtListView listTag = ((NbtListView) nbt).view();
            if (listTag.itemType() == itemType)
                return Optional.of(listTag);
        }
        return Optional.empty();
    }

    default @NotNull Optional<NbtObjectView> getObject(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtObjectView)
            return Optional.of(((NbtObjectView) nbt).view());
        return Optional.empty();
    }

    default @NotNull Optional<int[]> getIntArray(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtIntArrayView)
            return Optional.of(((NbtIntArrayView) nbt).toArray());
        return Optional.empty();
    }

    default @NotNull Optional<long[]> getLongArray(int i) {
        NbtElement nbt = get(i);
        if (nbt instanceof NbtLongArrayView)
            return Optional.of(((NbtLongArrayView) nbt).toArray());
        return Optional.empty();
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
