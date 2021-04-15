package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface CompoundTagView extends Tag {
    @Override
    default @NotNull TagType type() {
        return TagType.COMPOUND;
    }

    int size();
    boolean isEmpty();
    @Nullable Tag get(@NotNull String name);
    boolean containsName(@NotNull String name);
    boolean containsValue(@NotNull Tag value);
    @NotNull Set<@NotNull String> names();
    @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull Tag>> entries();

    default byte getByte(@NotNull String name, byte defaultValue) {
        Tag tag = get(name);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().byteValue();
        return defaultValue;
    }

    default boolean getBoolean(@NotNull String name, boolean defaultValue) {
        Tag tag = get(name);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().byteValue() > 0;
        return defaultValue;
    }

    default short getShort(@NotNull String name, short defaultValue) {
        Tag tag = get(name);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().shortValue();
        return defaultValue;
    }

    default int getInt(@NotNull String name, int defaultValue) {
        Tag tag = get(name);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().intValue();
        return defaultValue;
    }

    default long getLong(@NotNull String name, long defaultValue) {
        Tag tag = get(name);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().longValue();
        return defaultValue;
    }

    default float getFloat(@NotNull String name, float defaultValue) {
        Tag tag = get(name);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().floatValue();
        return defaultValue;
    }

    default double getDouble(@NotNull String name, double defaultValue) {
        Tag tag = get(name);
        if (tag instanceof NumberTag)
            return ((NumberTag) tag).valueAsNumber().doubleValue();
        return defaultValue;
    }

    default byte @NotNull [] getByteArray(@NotNull String name) {
        Tag tag = get(name);
        if (tag instanceof ByteArrayTagView)
            return ((ByteArrayTagView) tag).toArray();
        return EMPTY_BYTE_ARRAY;
    }

    default @NotNull String getString(@NotNull String name, @NotNull String defaultValue) {
        Tag tag = get(name);
        if (tag instanceof StringTag)
            return ((StringTag) tag).value();
        return defaultValue;
    }

    default @Nullable ListTagView getList(@NotNull String name, @NotNull TagType itemType) {
        Tag tag = get(name);
        if (tag instanceof ListTagView) {
            ListTagView listTag = (ListTagView) tag;
            if (listTag.itemType() == itemType)
                return listTag;
        }
        return null;
    }

    default @Nullable CompoundTagView getCompound(@NotNull String name) {
        Tag tag = get(name);
        if (tag instanceof CompoundTagView)
            return (CompoundTagView) tag;
        return null;
    }

    default int @NotNull [] getIntArray(@NotNull String name) {
        Tag tag = get(name);
        if (tag instanceof IntArrayTagView)
            return ((IntArrayTagView) tag).toArray();
        return EMPTY_INT_ARRAY;
    }

    default long @NotNull [] getLongArray(@NotNull String name) {
        Tag tag = get(name);
        if (tag instanceof LongArrayTagView)
            return ((LongArrayTagView) tag).toArray();
        return EMPTY_LONG_ARRAY;
    }

    default boolean contains(@NotNull String name, @NotNull TagType type) {
        Tag tag = get(name);
        if (tag == null)
            return false;
        return tag.type() == type;
    }

    default boolean containsNumber(@NotNull String name) {
        Tag tag = get(name);
        if (tag == null)
            return false;
        return tag.type().isNumber();
    }

    @Override
    default @NotNull CompoundTagView view() {
        return this;
    }

    @Override
    default @NotNull CompoundTagView copy() {
        return this;
    }
}
