package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface ListTagView extends RootTag, Iterable<Tag> {
    @Override
    default @NotNull TagType type() {
        return TagType.LIST;
    }

    @NotNull TagType itemType();
    int size();
    @NotNull Tag get(int i);

    default byte getByte(int i, byte defaultValue) {
        Tag tag = get(i);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().byteValue();
        return defaultValue;
    }

    default boolean getBoolean(int i, boolean defaultValue) {
        Tag tag = get(i);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().byteValue() > 0;
        return defaultValue;
    }

    default short getShort(int i, short defaultValue) {
        Tag tag = get(i);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().shortValue();
        return defaultValue;
    }

    default int getInt(int i, int defaultValue) {
        Tag tag = get(i);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().intValue();
        return defaultValue;
    }

    default long getLong(int i, long defaultValue) {
        Tag tag = get(i);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().longValue();
        return defaultValue;
    }

    default float getFloat(int i, float defaultValue) {
        Tag tag = get(i);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().floatValue();
        return defaultValue;
    }

    default double getDouble(int i, double defaultValue) {
        Tag tag = get(i);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().doubleValue();
        return defaultValue;
    }

    default byte @NotNull [] getByteArray(int i) {
        Tag tag = get(i);
        if (tag instanceof ByteArrayTagView)
            return ((ByteArrayTagView) tag).toArray();
        return EMPTY_BYTE_ARRAY;
    }

    default @NotNull String getString(int i, @NotNull String defaultValue) {
        Tag tag = get(i);
        if (tag instanceof StringTag)
            return ((StringTag) tag).value();
        return defaultValue;
    }

    default @Nullable ListTagView getList(int i, @NotNull TagType itemType) {
        Tag tag = get(i);
        if (tag instanceof ListTagView) {
            ListTagView listTag = (ListTagView) tag;
            if (listTag.itemType() == itemType)
                return listTag;
        }
        return null;
    }

    default @Nullable CompoundTagView getCompound(int i) {
        Tag tag = get(i);
        if (tag instanceof CompoundTagView)
            return (CompoundTagView) tag;
        return null;
    }

    default int @NotNull [] getIntArray(int i) {
        Tag tag = get(i);
        if (tag instanceof IntArrayTagView)
            return ((IntArrayTagView) tag).toArray();
        return EMPTY_INT_ARRAY;
    }

    default long @NotNull [] getLongArray(int i) {
        Tag tag = get(i);
        if (tag instanceof LongArrayTagView)
            return ((LongArrayTagView) tag).toArray();
        return EMPTY_LONG_ARRAY;
    }

    @Override
    @NotNull Iterator<Tag> iterator();

    @Override
    default @NotNull ListTagView view() {
        return this;
    }

    @Override
    default @NotNull ListTagView copy() {
        return this;
    }
}
