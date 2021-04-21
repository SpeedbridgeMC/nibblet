package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class NbtLongArray implements NbtElement, NbtLongArrayView {
    private final ArrayList<Long> backingList;
    private final NbtLongArrayView view;

    private NbtLongArray(@NotNull ArrayList<@NotNull Long> backingList) {
        this.backingList = backingList;
        view = new NbtLongArrayView() {
            @Override
            public int length() {
                return NbtLongArray.this.length();
            }

            @Override
            public long get(int i) {
                return NbtLongArray.this.get(i);
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

}
