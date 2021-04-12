package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class MutableCompoundTag extends CompoundTag {
    private MutableCompoundTag() {
        super(new LinkedHashMap<>(), true);
    }

    private MutableCompoundTag(int initialCapacity) {
        super(new LinkedHashMap<>(initialCapacity), true);
    }

    private MutableCompoundTag(@NotNull Map<@NotNull String, @NotNull Tag> entries) {
        super(new LinkedHashMap<>(entries), true);
    }

    public static @NotNull MutableCompoundTag create() {
        return new MutableCompoundTag();
    }

    public static @NotNull MutableCompoundTag create(int initialCapacity) {
        return new MutableCompoundTag(initialCapacity);
    }

    public static @NotNull MutableCompoundTag copyOf(@NotNull Map<@NotNull String, @NotNull Tag> entries) {
        return new MutableCompoundTag(entries);
    }

    @Override
    protected @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull Tag>> createEntrySet() {
        return backingMap.entrySet();
    }

    public @Nullable Tag put(@NotNull String name, @NotNull Tag value) {
        if (value == this)
            throw new IllegalArgumentException("Can't add tag as its own child!");
        return backingMap.put(name, value);
    }

    public void putByte(@NotNull String name, byte value) {
        put(name, ByteTag.of(value));
    }

    public void putBoolean(@NotNull String name, boolean value) {
        put(name, ByteTag.of((byte) (value ? 1 : 0)));
    }

    public void putShort(@NotNull String name, short value) {
        put(name, ShortTag.of(value));
    }

    public void putInt(@NotNull String name, int value) {
        put(name, IntTag.of(value));
    }

    public void putLong(@NotNull String name, long value) {
        put(name, LongTag.of(value));
    }

    public void putFloat(@NotNull String name, float value) {
        put(name, FloatTag.of(value));
    }

    public void putDouble(@NotNull String name, double value) {
        put(name, DoubleTag.of(value));
    }

    public void putByteArray(@NotNull String name, byte @NotNull ... values) {
        put(name, ByteArrayTag.copyOf(values));
    }

    public void putString(@NotNull String name, @NotNull String value) {
        put(name, StringTag.of(value));
    }

    public void putIntArray(@NotNull String name, int @NotNull ... values) {
        put(name, IntArrayTag.copyOf(values));
    }

    public void putLongArray(@NotNull String name, long @NotNull ... values) {
        put(name, LongArrayTag.copyOf(values));
    }

    public @Nullable Tag remove(@NotNull String name) {
        return backingMap.remove(name);
    }

    @Override
    public @NotNull MutableCompoundTag copy() {
        return new MutableCompoundTag(backingMap);
    }

    @Override
    public @NotNull MutableCompoundTag deepCopy() {
        MutableCompoundTag tag = new MutableCompoundTag(size());
        for (Map.Entry<String, Tag> entry : backingMap.entrySet())
            tag.put(entry.getKey(), entry.getValue().deepCopy());
        return tag;
    }

}
