package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class ListTag implements Tag, ListTagView {
    public static final class Builder {
        private final ArrayList<Tag> backingList;
        private TagType itemType;

        private Builder() {
            backingList = new ArrayList<>();
            itemType = TagType.END;
        }

        private Builder(int initialCapacity) {
            backingList = new ArrayList<>(initialCapacity);
            itemType = TagType.END;
        }

        public @NotNull Builder add(@NotNull Tag v) {
            if (itemType == TagType.END)
                itemType = v.type();
            else if (itemType != v.type())
                throw new IllegalArgumentException("Tried to add tag of type " + v.type() + " to list of type " + itemType + "!");
            backingList.add(v);
            return this;
        }

        public @NotNull Builder addByte(byte value) {
            return add(ByteTag.of(value));
        }

        public @NotNull Builder addBoolean(boolean value) {
            return add(ByteTag.of((byte) (value ? 1 : 0)));
        }

        public @NotNull Builder addShort(short value) {
            return add(ShortTag.of(value));
        }

        public @NotNull Builder addInt(int value) {
            return add(IntTag.of(value));
        }

        public @NotNull Builder addLong(long value) {
            return add(LongTag.of(value));
        }

        public @NotNull Builder addFloat(float value) {
            return add(FloatTag.of(value));
        }

        public @NotNull Builder addDouble(double value) {
            return add(DoubleTag.of(value));
        }

        public @NotNull Builder addByteArray(byte @NotNull ... values) {
            return add(ByteArrayTag.copyOf(values));
        }

        public @NotNull Builder addString(@NotNull String value) {
            return add(StringTag.of(value));
        }

        public @NotNull Builder addIntArray(int @NotNull ... values) {
            return add(IntArrayTag.copyOf(values));
        }

        public @NotNull Builder addLongArray(long @NotNull ... values) {
            return add(LongArrayTag.copyOf(values));
        }

        public @NotNull ListTag build() {
            return new ListTag(itemType, new ArrayList<>(backingList));
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    private final List<Tag> backingList;
    private TagType itemType;
    private final ListTagView view;

    private ListTag(@NotNull TagType itemType, @NotNull List<@NotNull Tag> backingList) {
        this.backingList = backingList;
        this.itemType = itemType;
        view = new ListTagView() {
            @Override
            public @NotNull TagType itemType() {
                return ListTag.this.itemType();
            }

            @Override
            public int size() {
                return ListTag.this.size();
            }

            @Override
            public @NotNull Tag get(int i) {
                return ListTag.this.get(i).view();
            }

            @Override
            public @NotNull Iterator<Tag> iterator() {
                return ListTag.this.iterator();
            }
        };
    }

    public static @NotNull ListTag create() {
        return new ListTag(TagType.END, new ArrayList<>());
    }

    public static @NotNull ListTag create(int initialCapacity) {
        return new ListTag(TagType.END, new ArrayList<>(initialCapacity));
    }

    @SafeVarargs
    public static <T extends Tag> @NotNull ListTag of(@NotNull T @NotNull ... values) {
        Builder builder = builder(values.length);
        for (T value : values)
            builder.add(value);
        return builder.build();
    }

    public static <T extends Tag> @NotNull ListTag copyOf(@NotNull Iterable<@NotNull T> values) {
        Builder builder = builder();
        for (Tag v : values)
            builder.add(v);
        return builder.build();
    }

    @Override
    public @NotNull TagType type() {
        return TagType.LIST;
    }

    @Override
    public @NotNull ListTagView view() {
        return view;
    }

    @Override
    public @NotNull TagType itemType() {
        return itemType;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public @NotNull Tag get(int i) {
        return backingList.get(i);
    }

    private void checkItemType(@NotNull Tag tag) {
        if (itemType == TagType.END)
            itemType = tag.type();
        else if (itemType != tag.type())
            throw new IllegalArgumentException("Tried to add tag of type " + tag.type() + " to list of type " + itemType + "!");
    }

    public @NotNull Tag set(int i, @NotNull Tag v) {
        checkItemType(v);
        return backingList.set(i, v);
    }

    public void setByte(int i, byte value) {
        set(i, ByteTag.of(value));
    }

    public void setBoolean(int i, boolean value) {
        set(i, ByteTag.of((byte) (value ? 1 : 0)));
    }

    public void setShort(int i, short value) {
        set(i, ShortTag.of(value));
    }

    public void setInt(int i, int value) {
        set(i, IntTag.of(value));
    }

    public void setLong(int i, long value) {
        set(i, LongTag.of(value));
    }

    public void setFloat(int i, float value) {
        set(i, FloatTag.of(value));
    }

    public void setDouble(int i, double value) {
        set(i, DoubleTag.of(value));
    }

    public void setByteArray(int i, byte @NotNull ... values) {
        set(i, ByteArrayTag.copyOf(values));
    }

    public void setString(int i, @NotNull String value) {
        set(i, StringTag.of(value));
    }

    public void setIntArray(int i, int @NotNull ... values) {
        set(i, IntArrayTag.copyOf(values));
    }

    public void setLongArray(int i, long @NotNull ... values) {
        set(i, LongArrayTag.copyOf(values));
    }

    public boolean add(@NotNull Tag v) {
        checkItemType(v);
        return backingList.add(v);
    }

    public void addByte(byte value) {
        add(ByteTag.of(value));
    }

    public void addBoolean(boolean value) {
        add(ByteTag.of((byte) (value ? 1 : 0)));
    }

    public void addShort(short value) {
        add(ShortTag.of(value));
    }

    public void addInt(int value) {
        add(IntTag.of(value));
    }

    public void addLong(long value) {
        add(LongTag.of(value));
    }

    public void addFloat(float value) {
        add(FloatTag.of(value));
    }

    public void addDouble(double value) {
        add(DoubleTag.of(value));
    }

    public void addByteArray(byte @NotNull ... values) {
        add(ByteArrayTag.copyOf(values));
    }

    public void addString(@NotNull String value) {
        add(StringTag.of(value));
    }

    public void addIntArray(int @NotNull ... values) {
        add(IntArrayTag.copyOf(values));
    }

    public void addLongArray(long @NotNull ... values) {
        add(LongArrayTag.copyOf(values));
    }

    public @NotNull Tag removeAt(int i) {
        return backingList.remove(i);
    }

    public boolean remove(@NotNull Tag v) {
        return backingList.remove(v);
    }

    public <T extends Tag> boolean addAll(@NotNull Iterable<@NotNull T> values) {
        boolean changed = false;
        for (Tag v : values)
            changed |= add(v);
        return changed;
    }

    public void clear() {
        backingList.clear();
        itemType = TagType.END;
    }

    @NotNull
    @Override
    public Iterator<Tag> iterator() {
        return new Iterator<Tag>() {
            private final Iterator<Tag> delegate = backingList.iterator();

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public Tag next() {
                return delegate.next();
            }

            @Override
            public void forEachRemaining(Consumer<? super Tag> action) {
                delegate.forEachRemaining(action);
            }
        };
    }

    @Override
    public @NotNull ListTag copy() {
        return new ListTag(itemType, backingList);
    }

    @Override
    public @NotNull ListTag deepCopy() {
        ListTag.Builder builder = builder(backingList.size());
        for (Tag tag : this)
            builder.add(tag);
        return builder.build();
    }
}
