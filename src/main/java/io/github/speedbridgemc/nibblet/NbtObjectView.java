package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public interface NbtObjectView extends NbtRootElement {
    final class Entry {
        private final @NotNull String name;
        private final @NotNull NbtElement element;
        private final @NotNull NbtType elementType;

        public Entry(@NotNull String name, @NotNull NbtElement element) {
            this.name = name;
            this.element = element;
            elementType = element.type();
        }

        public @NotNull String name() {
            return name;
        }

        public @NotNull NbtElement element() {
            return element;
        }

        public @NotNull NbtType elementType() {
            return elementType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Entry entry = (Entry) o;
            return name.equals(entry.name) && element.equals(entry.element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, element);
        }
    }

    @Override
    default @NotNull NbtType type() {
        return NbtType.OBJECT;
    }

    int size();
    boolean isEmpty();
    @Nullable NbtElement get(@NotNull String name);
    boolean containsName(@NotNull String name);
    boolean containsElement(@NotNull NbtElement element);
    @NotNull Iterable<@NotNull String> names();
    @NotNull Iterable<@NotNull Entry> entries();

    default byte getByte(@NotNull String name, byte defaultValue) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().byteValue();
        return defaultValue;
    }

    default boolean getBoolean(@NotNull String name, boolean defaultValue) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().byteValue() > 0;
        return defaultValue;
    }

    default short getShort(@NotNull String name, short defaultValue) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().shortValue();
        return defaultValue;
    }

    default int getInt(@NotNull String name, int defaultValue) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().intValue();
        return defaultValue;
    }

    default long getLong(@NotNull String name, long defaultValue) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().longValue();
        return defaultValue;
    }

    default float getFloat(@NotNull String name, float defaultValue) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().floatValue();
        return defaultValue;
    }

    default double getDouble(@NotNull String name, double defaultValue) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtNumber)
            return ((NbtNumber) nbt).valueAsNumber().doubleValue();
        return defaultValue;
    }

    default @NotNull Optional<byte[]> getByteArray(@NotNull String name) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtByteArrayView)
            return Optional.of(((NbtByteArrayView) nbt).toArray());
        return Optional.empty();
    }

    default @NotNull Optional<String> getString(@NotNull String name) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtString)
            return Optional.of(((NbtString) nbt).value());
        return Optional.empty();
    }

    default @NotNull Optional<NbtListView> getList(@NotNull String name, @NotNull NbtType itemType) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtListView) {
            NbtListView listTag = ((NbtListView) nbt).view();
            if (listTag.itemType() == itemType)
                return Optional.of(listTag);
        }
        return Optional.empty();
    }

    default @NotNull Optional<NbtObjectView> getObject(@NotNull String name) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtObjectView)
            return Optional.of(((NbtObjectView) nbt).view());
        return Optional.empty();
    }

    default @NotNull Optional<int[]> getIntArray(@NotNull String name) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtIntArrayView)
            return Optional.of(((NbtIntArrayView) nbt).toArray());
        return Optional.empty();
    }

    default @NotNull Optional<long[]> getLongArray(@NotNull String name) {
        NbtElement nbt = get(name);
        if (nbt instanceof NbtLongArrayView)
            return Optional.of(((NbtLongArrayView) nbt).toArray());
        return Optional.empty();
    }

    default boolean contains(@NotNull String name, @NotNull NbtType type) {
        NbtElement nbt = get(name);
        if (nbt == null)
            return false;
        return nbt.type() == type;
    }

    default boolean containsNumber(@NotNull String name) {
        NbtElement nbt = get(name);
        if (nbt == null)
            return false;
        return nbt.type().isNumber();
    }

    @Override
    default @NotNull NbtObjectView view() {
        return this;
    }

    @Override
    default @NotNull NbtObjectView copy() {
        return this;
    }
}
