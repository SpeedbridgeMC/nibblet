package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class NbtObject implements NbtElement, NbtObjectView {
    public static final class Builder {
        private final LinkedHashMap<String, NbtElement> backingMap;

        private Builder() {
            backingMap = new LinkedHashMap<>();
        }

        private Builder(int initialCapacity) {
            backingMap = new LinkedHashMap<>(initialCapacity);
        }

        public @NotNull Builder put(@NotNull String name, @NotNull NbtElement value) {
            backingMap.put(name, value);
            return this;
        }

        public @NotNull Builder putByte(@NotNull String name, byte value) {
            return put(name, NbtByte.of(value));
        }

        public @NotNull Builder putBoolean(@NotNull String name, boolean value) {
            return put(name, NbtByte.of((byte) (value ? 1 : 0)));
        }

        public @NotNull Builder putShort(@NotNull String name, short value) {
            return put(name, NbtShort.of(value));
        }

        public @NotNull Builder putInt(@NotNull String name, int value) {
            return put(name, NbtInt.of(value));
        }

        public @NotNull Builder putLong(@NotNull String name, long value) {
            return put(name, NbtLong.of(value));
        }

        public @NotNull Builder putFloat(@NotNull String name, float value) {
            return put(name, NbtFloat.of(value));
        }

        public @NotNull Builder putDouble(@NotNull String name, double value) {
            return put(name, NbtDouble.of(value));
        }

        public @NotNull Builder putByteArray(@NotNull String name, byte @NotNull ... values) {
            return put(name, NbtByteArray.copyOf(values));
        }

        public @NotNull Builder putString(@NotNull String name, @NotNull String value) {
            return put(name, NbtString.of(value));
        }

        public @NotNull Builder putIntArray(@NotNull String name, int @NotNull ... values) {
            return put(name, NbtIntArray.copyOf(values));
        }

        public @NotNull Builder putLongArray(@NotNull String name, long @NotNull ... values) {
            return put(name, NbtLongArray.copyOf(values));
        }

        public @NotNull NbtObject build() {
            return new NbtObject(new LinkedHashMap<>(backingMap));
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    private final Map<String, NbtElement> backingMap;
    private final Set<String> nameSet;
    private final NbtObjectView view;
    private Set<Map.Entry<String, NbtElement>> entrySet;

    private NbtObject(@NotNull LinkedHashMap<@NotNull String, @NotNull NbtElement> backingMap) {
        this.backingMap = backingMap;
        nameSet = Collections.unmodifiableSet(this.backingMap.keySet());
        view = new NbtObjectView() {
            @Override
            public int size() {
                return NbtObject.this.size();
            }

            @Override
            public boolean isEmpty() {
                return NbtObject.this.isEmpty();
            }

            @Override
            public @Nullable NbtElement get(@NotNull String name) {
                NbtElement nbt = NbtObject.this.get(name);
                if (nbt == null)
                    return null;
                return nbt.view();
            }

            @Override
            public boolean containsName(@NotNull String name) {
                return NbtObject.this.containsName(name);
            }

            @Override
            public boolean containsValue(@NotNull NbtElement value) {
                return NbtObject.this.containsValue(value);
            }

            @Override
            public @NotNull Set<@NotNull String> names() {
                return NbtObject.this.names();
            }

            @Override
            public @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull NbtElement>> entries() {
                return NbtObject.this.entries();
            }
        };
    }

    private NbtObject() {
        this(new LinkedHashMap<>());
    }

    private NbtObject(int initialCapacity) {
        this(new LinkedHashMap<>(initialCapacity));
    }

    public static @NotNull NbtObject create() {
        return new NbtObject();
    }

    public static @NotNull NbtObject create(int initialCapacity) {
        return new NbtObject(initialCapacity);
    }

    public static @NotNull NbtObject copyOf(@NotNull Map<@NotNull String, @NotNull NbtElement> entries) {
        return new NbtObject(new LinkedHashMap<>(entries));
    }

    @Override
    public @NotNull NbtObjectView view() {
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
    public @Nullable NbtElement get(@NotNull String name) {
        return backingMap.get(name);
    }

    @Override
    public boolean containsName(@NotNull String name) {
        return backingMap.containsKey(name);
    }

    @Override
    public boolean containsValue(@NotNull NbtElement value) {
        return backingMap.containsValue(value);
    }

    @Override
    public @NotNull Set<String> names() {
        return nameSet;
    }

    @Override
    public @NotNull Set<Map.Entry<String, NbtElement>> entries() {
        if (entrySet == null)
            entrySet = Collections.unmodifiableMap(backingMap).entrySet();
        return entrySet;
    }

    public @Nullable NbtElement put(@NotNull String name, @NotNull NbtElement value) {
        if (value == this)
            throw new IllegalArgumentException("Can't add tag as its own child!");
        return backingMap.put(name, value);
    }

    public void putByte(@NotNull String name, byte value) {
        put(name, NbtByte.of(value));
    }

    public void putBoolean(@NotNull String name, boolean value) {
        put(name, NbtByte.of((byte) (value ? 1 : 0)));
    }

    public void putShort(@NotNull String name, short value) {
        put(name, NbtShort.of(value));
    }

    public void putInt(@NotNull String name, int value) {
        put(name, NbtInt.of(value));
    }

    public void putLong(@NotNull String name, long value) {
        put(name, NbtLong.of(value));
    }

    public void putFloat(@NotNull String name, float value) {
        put(name, NbtFloat.of(value));
    }

    public void putDouble(@NotNull String name, double value) {
        put(name, NbtDouble.of(value));
    }

    public void putByteArray(@NotNull String name, byte @NotNull ... values) {
        put(name, NbtByteArray.copyOf(values));
    }

    public void putString(@NotNull String name, @NotNull String value) {
        put(name, NbtString.of(value));
    }

    public void putIntArray(@NotNull String name, int @NotNull ... values) {
        put(name, NbtIntArray.copyOf(values));
    }

    public void putLongArray(@NotNull String name, long @NotNull ... values) {
        put(name, NbtLongArray.copyOf(values));
    }

    public @Nullable NbtElement remove(@NotNull String name) {
        return backingMap.remove(name);
    }

    @Override
    public @NotNull NbtObject copy() {
        return new NbtObject(new LinkedHashMap<>(backingMap));
    }

    @Override
    public @NotNull NbtObject deepCopy() {
        NbtObject.Builder builder = builder(backingMap.size());
        for (Map.Entry<String, NbtElement> entry : entries())
            builder.put(entry.getKey(), entry.getValue().deepCopy());
        return builder.build();
    }
}
