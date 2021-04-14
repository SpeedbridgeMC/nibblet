package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class CompoundTag implements Tag, CompoundTagView {
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
            return new CompoundTag(new LinkedHashMap<>(backingMap));
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    private final Map<String, Tag> backingMap;
    private final Set<String> nameSet;
    private final CompoundTagView view;
    private Set<Map.Entry<String, Tag>> entrySet;

    private CompoundTag(@NotNull LinkedHashMap<@NotNull String, @NotNull Tag> backingMap) {
        this.backingMap = backingMap;
        nameSet = Collections.unmodifiableSet(this.backingMap.keySet());
        view = new CompoundTagView() {
            @Override
            public int size() {
                return CompoundTag.this.size();
            }

            @Override
            public boolean isEmpty() {
                return CompoundTag.this.isEmpty();
            }

            @Override
            public @Nullable Tag get(@NotNull String name) {
                Tag tag = CompoundTag.this.get(name);
                if (tag == null)
                    return null;
                return tag.view();
            }

            @Override
            public boolean containsName(@NotNull String name) {
                return CompoundTag.this.containsName(name);
            }

            @Override
            public boolean containsValue(@NotNull Tag value) {
                return CompoundTag.this.containsValue(value);
            }

            @Override
            public @NotNull Set<@NotNull String> names() {
                return CompoundTag.this.names();
            }

            @Override
            public @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull Tag>> entries() {
                return CompoundTag.this.entries();
            }
        };
    }

    private CompoundTag() {
        this(new LinkedHashMap<>());
    }

    private CompoundTag(int initialCapacity) {
        this(new LinkedHashMap<>(initialCapacity));
    }

    public static @NotNull CompoundTag create() {
        return new CompoundTag();
    }

    public static @NotNull CompoundTag create(int initialCapacity) {
        return new CompoundTag(initialCapacity);
    }

    public static @NotNull CompoundTag copyOf(@NotNull Map<@NotNull String, @NotNull Tag> entries) {
        return new CompoundTag(new LinkedHashMap<>(entries));
    }

    @Override
    public @NotNull CompoundTagView view() {
        return view;
    }

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public @Nullable Tag get(@NotNull String name) {
        return backingMap.get(name);
    }

    @Override
    public boolean containsName(@NotNull String name) {
        return backingMap.containsKey(name);
    }

    @Override
    public boolean containsValue(@NotNull Tag value) {
        return backingMap.containsValue(value);
    }

    @Override
    public @NotNull Set<String> names() {
        return nameSet;
    }

    @Override
    public @NotNull Set<Map.Entry<String, Tag>> entries() {
        if (entrySet == null)
            entrySet = Collections.unmodifiableMap(backingMap).entrySet();
        return entrySet;
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
    public @NotNull CompoundTag copy() {
        return new CompoundTag(new LinkedHashMap<>(backingMap));
    }

    @Override
    public @NotNull CompoundTag deepCopy() {
        CompoundTag.Builder builder = builder(backingMap.size());
        for (Map.Entry<String, Tag> entry : entries())
            builder.put(entry.getKey(), entry.getValue().deepCopy());
        return builder.build();
    }
}
