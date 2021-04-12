package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CompoundTag implements Tag {
    public static final class Builder {
        private final LinkedHashMap<String, Tag> backingMap;

        private Builder() {
            backingMap = new LinkedHashMap<>();
        }

        private Builder(int initialCapacity) {
            backingMap = new LinkedHashMap<>(initialCapacity);
        }

        public @NotNull Builder put(@NotNull String name, @NotNull Tag value) {
            backingMap.put(name, value);
            return this;
        }

        public @NotNull Builder putByte(@NotNull String name, byte value) {
            return put(name, ByteTag.of(value));
        }

        public @NotNull Builder putBoolean(@NotNull String name, boolean value) {
            return put(name, ByteTag.of((byte) (value ? 1 : 0)));
        }

        public @NotNull Builder putShort(@NotNull String name, short value) {
            return put(name, ShortTag.of(value));
        }

        public @NotNull Builder putInt(@NotNull String name, int value) {
            return put(name, IntTag.of(value));
        }

        public @NotNull Builder putLong(@NotNull String name, long value) {
            return put(name, LongTag.of(value));
        }

        public @NotNull Builder putFloat(@NotNull String name, float value) {
            return put(name, FloatTag.of(value));
        }

        public @NotNull Builder putDouble(@NotNull String name, double value) {
            return put(name, DoubleTag.of(value));
        }

        public @NotNull Builder putByteArray(@NotNull String name, byte @NotNull ... values) {
            return put(name, ByteArrayTag.copyOf(values));
        }

        public @NotNull Builder putString(@NotNull String name, @NotNull String value) {
            return put(name, StringTag.of(value));
        }

        public @NotNull Builder putIntArray(@NotNull String name, int @NotNull ... values) {
            return put(name, IntArrayTag.copyOf(values));
        }

        public @NotNull Builder putLongArray(@NotNull String name, long @NotNull ... values) {
            return put(name, LongArrayTag.copyOf(values));
        }

        public @NotNull CompoundTag build() {
            return new CompoundTag(backingMap);
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    protected final Map<String, Tag> backingMap;
    protected Set<String> nameSet;
    protected Set<Map.Entry<String, Tag>> entrySet;

    CompoundTag(@NotNull Map<@NotNull String, @NotNull Tag> backingMap, boolean mutable) {
        this.backingMap = backingMap;
        if (mutable)
            nameSet = backingMap.keySet();
        else
            nameSet = Collections.unmodifiableSet(backingMap.keySet());
    }

    CompoundTag(@NotNull Map<@NotNull String, @NotNull Tag> entries) {
        this(new LinkedHashMap<>(entries), false);
    }

    public static @NotNull CompoundTag copyOf(@NotNull Map<@NotNull String, @NotNull Tag> entries) {
        return new CompoundTag(entries);
    }

    @Override
    public @NotNull TagType type() {
        return TagType.COMPOUND;
    }

    public int size() {
        return backingMap.size();
    }

    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    public @Nullable Tag get(@NotNull String name) {
        return backingMap.get(name);
    }

    public byte getByte(@NotNull String name, byte defaultValue) {
        Tag tag = get(name);
        if (tag instanceof ByteTag)
            return ((ByteTag) tag).value();
        return defaultValue;
    }

    public boolean getBoolean(@NotNull String name, boolean defaultValue) {
        Tag tag = get(name);
        if (tag instanceof ByteTag)
            return ((ByteTag) tag).value() != 0;
        return defaultValue;
    }

    public short getShort(@NotNull String name, short defaultValue) {
        Tag tag = get(name);
        if (tag instanceof ByteTag)
            return ((ByteTag) tag).value();
        else if (tag instanceof ShortTag)
            return ((ShortTag) tag).value();
        return defaultValue;
    }

    public int getInt(@NotNull String name, int defaultValue) {
        Tag tag = get(name);
        if (tag instanceof ByteTag)
            return ((ByteTag) tag).value();
        else if (tag instanceof ShortTag)
            return ((ShortTag) tag).value();
        else if (tag instanceof IntTag)
            return ((IntTag) tag).value();
        return defaultValue;
    }

    public long getLong(@NotNull String name, long defaultValue) {
        Tag tag = get(name);
        if (tag instanceof ByteTag)
            return ((ByteTag) tag).value();
        else if (tag instanceof ShortTag)
            return ((ShortTag) tag).value();
        else if (tag instanceof IntTag)
            return ((IntTag) tag).value();
        else if (tag instanceof LongTag)
            return ((LongTag) tag).value();
        return defaultValue;
    }

    public float getFloat(@NotNull String name, float defaultValue) {
        Tag tag = get(name);
        if (tag instanceof ByteTag)
            return ((ByteTag) tag).value();
        else if (tag instanceof ShortTag)
            return ((ShortTag) tag).value();
        else if (tag instanceof IntTag)
            return ((IntTag) tag).value();
        else if (tag instanceof FloatTag)
            return ((FloatTag) tag).value();
        return defaultValue;
    }

    public double getDouble(@NotNull String name, double defaultValue) {
        Tag tag = get(name);
        if (tag instanceof ByteTag)
            return ((ByteTag) tag).value();
        else if (tag instanceof ShortTag)
            return ((ShortTag) tag).value();
        else if (tag instanceof IntTag)
            return ((IntTag) tag).value();
        else if (tag instanceof LongTag)
            return ((LongTag) tag).value();
        else if (tag instanceof FloatTag)
            return ((FloatTag) tag).value();
        else if (tag instanceof DoubleTag)
            return ((DoubleTag) tag).value();
        return defaultValue;
    }

    private final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public byte @NotNull [] getByteArray(@NotNull String name) {
        Tag tag = get(name);
        if (tag instanceof ByteArrayTag)
            return ((ByteArrayTag) tag).toArray();
        return EMPTY_BYTE_ARRAY;
    }

    public @NotNull String getString(@NotNull String name, @NotNull String defaultValue) {
        Tag tag = get(name);
        if (tag instanceof StringTag)
            return ((StringTag) tag).value();
        return defaultValue;
    }

    private final int[] EMPTY_INT_ARRAY = new int[0];
    public int @NotNull [] getIntArray(@NotNull String name) {
        Tag tag = get(name);
        if (tag instanceof IntArrayTag)
            return ((IntArrayTag) tag).toArray();
        return EMPTY_INT_ARRAY;
    }

    private final long[] EMPTY_LONG_ARRAY = new long[0];
    public long @NotNull [] getLongArray(@NotNull String name) {
        Tag tag = get(name);
        if (tag instanceof LongArrayTag)
            return ((LongArrayTag) tag).toArray();
        return EMPTY_LONG_ARRAY;
    }

    public boolean contains(@NotNull String name, @NotNull TagType type) {
        Tag tag = get(name);
        if (tag == null)
            return false;
        if (type == TagType.NUMBER)
            return tag.type().isNumber();
        return tag.type() == type;
    }

    public boolean containsName(@NotNull String name) {
        return backingMap.containsKey(name);
    }

    public boolean containsValue(@NotNull Tag value) {
        return backingMap.containsValue(value);
    }

    public @NotNull Set<@NotNull String> names() {
        return nameSet;
    }

    protected @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull Tag>> createEntrySet() {
        return Collections.unmodifiableMap(backingMap).entrySet();
    }

    public @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull Tag>> entries() {
        if (entrySet == null)
            entrySet = createEntrySet();
        return entrySet;
    }

    @Override
    public @NotNull CompoundTag copy() {
        return this;
    }

    @Override
    public @NotNull MutableCompoundTag mutableCopy() {
        MutableCompoundTag tag = MutableCompoundTag.create(size());
        for (Map.Entry<String, Tag> entry : backingMap.entrySet())
            tag.put(entry.getKey(), entry.getValue().mutableCopy());
        return tag;
    }
}
