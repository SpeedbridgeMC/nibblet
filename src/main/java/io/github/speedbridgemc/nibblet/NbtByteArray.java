package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class NbtByteArray implements NbtElement, NbtByteArrayView {
    public static final class Builder {
        private final ArrayList<Byte> backingList;

        private Builder() {
            backingList = new ArrayList<>();
        }

        private Builder(int initialCapacity) {
            backingList = new ArrayList<>(initialCapacity);
        }

        public @NotNull Builder add(byte value) {
            backingList.add(value);
            return this;
        }

        public @NotNull Builder add(byte @NotNull ... values) {
            for (byte value : values)
                backingList.add(value);
            return this;
        }

        public @NotNull NbtByteArray build() {
            return new NbtByteArray(backingList);
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    private final ArrayList<Byte> backingList;
    private final List<Byte> backingListU;
    private final NbtByteArrayView view;

    private NbtByteArray(ArrayList<Byte> backingList) {
        this.backingList = backingList;
        backingListU = Collections.unmodifiableList(backingList);
        view = new NbtByteArrayView() {
            @Override
            public int length() {
                return NbtByteArray.this.length();
            }

            @Override
            public byte get(int i) {
                return NbtByteArray.this.get(i);
            }

            @Override
            public @NotNull Iterator<@NotNull Byte> iterator() {
                return NbtByteArray.this.iterator();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this)
                    return true;
                if (!(obj instanceof NbtByteArrayView))
                    return false;
                return NbtByteArray.this.equals(obj);
            }

            @Override
            public int hashCode() {
                return NbtByteArray.this.hashCode();
            }
        };
    }

    private NbtByteArray() {
        this(new ArrayList<>());
    }

    private NbtByteArray(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    public static @NotNull NbtByteArray create() {
        return new NbtByteArray();
    }

    public static @NotNull NbtByteArray create(int initialCapacity) {
        return new NbtByteArray(initialCapacity);
    }

    public static @NotNull NbtByteArray copyOf(byte @NotNull ... values) {
        ArrayList<Byte> backingList = new ArrayList<>(values.length);
        for (byte value : values)
            backingList.add(value);
        return new NbtByteArray(backingList);
    }

    @Override
    public @NotNull NbtByteArrayView view() {
        return view;
    }

    @Override
    public int length() {
        return backingList.size();
    }

    @Override
    public byte get(int i) {
        return backingList.get(i);
    }

    @Override
    public @NotNull Iterator<@NotNull Byte> iterator() {
        return backingListU.iterator();
    }

    public byte set(int i, byte v) {
        return backingList.set(i, v);
    }

    public boolean add(byte v) {
        return backingList.add(v);
    }

    public byte removeAt(int i) {
        return backingList.remove(i);
    }

    public boolean remove(byte v) {
        return backingList.remove((Byte) v);
    }

    @Override
    public @NotNull NbtByteArray copy() {
        return new NbtByteArray(new ArrayList<>(backingList));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof NbtByteArrayView))
            return false;
        Iterator<Byte> e1 = iterator();
        Iterator<Byte> e2 = ((NbtByteArrayView) obj).iterator();
        while (e1.hasNext() && e2.hasNext()) {
            byte o1 = e1.next();
            byte o2 = e2.next();
            if (o1 != o2)
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.BYTE_ARRAY, backingList);
    }
}
