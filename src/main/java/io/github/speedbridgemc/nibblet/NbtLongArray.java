package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class NbtLongArray implements NbtElement, NbtLongArrayView {
    public static final class Builder {
        private final ArrayList<Long> backingList;

        private Builder() {
            backingList = new ArrayList<>();
        }

        private Builder(int initialCapacity) {
            backingList = new ArrayList<>(initialCapacity);
        }

        public @NotNull Builder add(long value) {
            backingList.add(value);
            return this;
        }

        public @NotNull Builder add(long @NotNull ... values) {
            for (long value : values)
                backingList.add(value);
            return this;
        }

        public @NotNull NbtLongArray build() {
            return new NbtLongArray(new ArrayList<>(backingList));
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    private final ArrayList<Long> backingList;
    private final List<Long> backingListU;
    private final NbtLongArrayView view;

    private NbtLongArray(@NotNull ArrayList<@NotNull Long> backingList) {
        this.backingList = backingList;
        backingListU = Collections.unmodifiableList(backingList);
        view = new NbtLongArrayView() {
            @Override
            public int length() {
                return NbtLongArray.this.length();
            }

            @Override
            public long get(int i) {
                return NbtLongArray.this.get(i);
            }

            @Override
            public @NotNull Iterator<@NotNull Long> iterator() {
                return NbtLongArray.this.iterator();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this)
                    return true;
                if (!(obj instanceof NbtLongArrayView))
                    return false;
                return NbtLongArray.this.equals(obj);
            }

            @Override
            public int hashCode() {
                return NbtLongArray.this.hashCode();
            }
        };
    }

    private NbtLongArray() {
        this(new ArrayList<>()) ;
    }

    private NbtLongArray(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    public static @NotNull NbtLongArray create() {
        return new NbtLongArray();
    }

    public static @NotNull NbtLongArray create(int initialCapacity) {
        return new NbtLongArray(initialCapacity);
    }

    public static @NotNull NbtLongArray copyOf(long @NotNull ... values) {
        ArrayList<Long> list = new ArrayList<>(values.length);
        for (long v : values)
            list.add(v);
        return new NbtLongArray(list);
    }

    @Override
    public @NotNull NbtLongArrayView view() {
        return view;
    }

    @Override
    public int length() {
        return backingList.size();
    }

    @Override
    public long get(int i) {
        return backingList.get(i);
    }

    @Override
    public @NotNull Iterator<@NotNull Long> iterator() {
        return backingListU.iterator();
    }

    public long set(int i, long v) {
        return backingList.set(i, v);
    }

    public boolean add(long v) {
        return backingList.add(v);
    }

    public long removeAt(int i) {
        return backingList.remove(i);
    }

    public boolean remove(long v) {
        return backingList.remove(v);
    }

    @Override
    public @NotNull NbtLongArray copy() {
        return new NbtLongArray(new ArrayList<>(backingList));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof NbtLongArrayView))
            return false;
        Iterator<Long> e1 = iterator();
        Iterator<Long> e2 = ((NbtLongArrayView) obj).iterator();
        while (e1.hasNext() && e2.hasNext()) {
            long o1 = e1.next();
            long o2 = e2.next();
            if (o1 != o2)
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(NbtType.LONG_ARRAY, backingList);
    }
}
