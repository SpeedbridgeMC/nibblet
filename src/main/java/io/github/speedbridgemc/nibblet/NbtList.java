package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class NbtList implements NbtElement, NbtListView {
    public static final class Builder {
        private final ArrayList<NbtElement> backingList;
        private NbtType itemType;

        private Builder() {
            backingList = new ArrayList<>();
            itemType = NbtType.END;
        }

        private Builder(int initialCapacity) {
            backingList = new ArrayList<>(initialCapacity);
            itemType = NbtType.END;
        }

        public @NotNull Builder add(@NotNull NbtElement value) {
            if (itemType == NbtType.END)
                itemType = value.type();
            else if (itemType != value.type())
                throw new IllegalArgumentException("Tried to add tag of type " + value.type() + " to list of type " + itemType + "!");
            backingList.add(value);
            return this;
        }

        @SafeVarargs
        public final <T extends NbtElement> @NotNull Builder add(@NotNull T @NotNull ... values) {
            for (T value : values)
                add(value);
            return this;
        }

        public @NotNull Builder addByte(byte value) {
            return add(NbtByte.of(value));
        }

        public @NotNull Builder addBoolean(boolean value) {
            return add(NbtByte.of((byte) (value ? 1 : 0)));
        }

        public @NotNull Builder addShort(short value) {
            return add(NbtShort.of(value));
        }

        public @NotNull Builder addInt(int value) {
            return add(NbtInt.of(value));
        }

        public @NotNull Builder addLong(long value) {
            return add(NbtLong.of(value));
        }

        public @NotNull Builder addFloat(float value) {
            return add(NbtFloat.of(value));
        }

        public @NotNull Builder addDouble(double value) {
            return add(NbtDouble.of(value));
        }

        public @NotNull Builder addByteArray(byte @NotNull ... values) {
            return add(NbtByteArray.copyOf(values));
        }

        public @NotNull Builder addString(@NotNull String value) {
            return add(NbtString.of(value));
        }

        public @NotNull Builder addIntArray(int @NotNull ... values) {
            return add(NbtIntArray.copyOf(values));
        }

        public @NotNull Builder addLongArray(long @NotNull ... values) {
            return add(NbtLongArray.copyOf(values));
        }

        public @NotNull NbtList build() {
            return new NbtList(itemType, new ArrayList<>(backingList));
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    private final ArrayList<NbtElement> backingList;
    private final List<NbtElement> backingListU;
    private NbtType itemType;
    private final NbtListView view;

    private NbtList(@NotNull NbtType itemType, @NotNull ArrayList<@NotNull NbtElement> backingList) {
        this.backingList = backingList;
        backingListU = Collections.unmodifiableList(backingList);
        this.itemType = itemType;
        view = new NbtListView() {
            @Override
            public @NotNull NbtType itemType() {
                return NbtList.this.itemType();
            }

            @Override
            public int size() {
                return NbtList.this.size();
            }

            @Override
            public @NotNull NbtElement get(int i) {
                return NbtList.this.get(i).view();
            }

            @Override
            public @NotNull Iterator<@NotNull NbtElement> iterator() {
                return new Iterator<NbtElement>() {
                    private final Iterator<NbtElement> delegate = NbtList.this.iterator();

                    @Override
                    public boolean hasNext() {
                        return delegate.hasNext();
                    }

                    @Override
                    public NbtElement next() {
                        NbtElement next = delegate.next();
                        if (next == null)
                            return null;
                        return next.view();
                    }
                };
            }
        };
    }

    public static @NotNull NbtList create() {
        return new NbtList(NbtType.END, new ArrayList<>());
    }

    public static @NotNull NbtList create(int initialCapacity) {
        return new NbtList(NbtType.END, new ArrayList<>(initialCapacity));
    }

    @SafeVarargs
    public static <T extends NbtElement> @NotNull NbtList of(@NotNull T @NotNull ... values) {
        Builder builder = builder(values.length);
        for (T value : values)
            builder.add(value);
        return builder.build();
    }

    public static <T extends NbtElement> @NotNull NbtList copyOf(@NotNull Iterable<@NotNull T> values) {
        Builder builder = builder();
        for (NbtElement v : values)
            builder.add(v);
        return builder.build();
    }

    @Override
    public @NotNull NbtListView view() {
        return view;
    }

    @Override
    public @NotNull NbtType itemType() {
        return itemType;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public @NotNull NbtElement get(int i) {
        return backingList.get(i);
    }

    private void checkItemType(@NotNull NbtElement nbt) {
        if (itemType == NbtType.END)
            itemType = nbt.type();
        else if (itemType != nbt.type())
            throw new IllegalArgumentException("Tried to add tag of type " + nbt.type() + " to list of type " + itemType + "!");
    }

    public @NotNull NbtElement set(int i, @NotNull NbtElement v) {
        checkItemType(v);
        return backingList.set(i, v);
    }

    public void setByte(int i, byte value) {
        set(i, NbtByte.of(value));
    }

    public void setBoolean(int i, boolean value) {
        set(i, NbtByte.of((byte) (value ? 1 : 0)));
    }

    public void setShort(int i, short value) {
        set(i, NbtShort.of(value));
    }

    public void setInt(int i, int value) {
        set(i, NbtInt.of(value));
    }

    public void setLong(int i, long value) {
        set(i, NbtLong.of(value));
    }

    public void setFloat(int i, float value) {
        set(i, NbtFloat.of(value));
    }

    public void setDouble(int i, double value) {
        set(i, NbtDouble.of(value));
    }

    public void setByteArray(int i, byte @NotNull ... values) {
        set(i, NbtByteArray.copyOf(values));
    }

    public void setString(int i, @NotNull String value) {
        set(i, NbtString.of(value));
    }

    public void setIntArray(int i, int @NotNull ... values) {
        set(i, NbtIntArray.copyOf(values));
    }

    public void setLongArray(int i, long @NotNull ... values) {
        set(i, NbtLongArray.copyOf(values));
    }

    public boolean add(@NotNull NbtElement v) {
        checkItemType(v);
        return backingList.add(v);
    }

    public void addByte(byte value) {
        add(NbtByte.of(value));
    }

    public void addBoolean(boolean value) {
        add(NbtByte.of((byte) (value ? 1 : 0)));
    }

    public void addShort(short value) {
        add(NbtShort.of(value));
    }

    public void addInt(int value) {
        add(NbtInt.of(value));
    }

    public void addLong(long value) {
        add(NbtLong.of(value));
    }

    public void addFloat(float value) {
        add(NbtFloat.of(value));
    }

    public void addDouble(double value) {
        add(NbtDouble.of(value));
    }

    public void addByteArray(byte @NotNull ... values) {
        add(NbtByteArray.copyOf(values));
    }

    public void addString(@NotNull String value) {
        add(NbtString.of(value));
    }

    public void addIntArray(int @NotNull ... values) {
        add(NbtIntArray.copyOf(values));
    }

    public void addLongArray(long @NotNull ... values) {
        add(NbtLongArray.copyOf(values));
    }

    public @NotNull NbtElement removeAt(int i) {
        return backingList.remove(i);
    }

    public boolean remove(@NotNull NbtElement v) {
        return backingList.remove(v);
    }

    public <T extends NbtElement> boolean addAll(@NotNull Iterable<@NotNull T> values) {
        boolean changed = false;
        for (NbtElement v : values)
            changed |= add(v);
        return changed;
    }

    public void clear() {
        backingList.clear();
        itemType = NbtType.END;
    }

    @Override
    public @NotNull Iterator<@NotNull NbtElement> iterator() {
        return backingListU.iterator();
    }

    @Override
    public @NotNull NbtList copy() {
        return new NbtList(itemType, backingList);
    }

    @Override
    public @NotNull NbtList deepCopy() {
        NbtList.Builder builder = builder(backingList.size());
        for (NbtElement nbt : this)
            builder.add(nbt.deepCopy());
        return builder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof NbtListView))
            return false;
        Iterator<NbtElement> e1 = iterator();
        Iterator<NbtElement> e2 = ((NbtListView) obj).iterator();
        while (e1.hasNext() && e2.hasNext()) {
            NbtElement o1 = e1.next();
            NbtElement o2 = e2.next();
            if (!o1.equals(o2))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.LIST, backingList);
    }
}
