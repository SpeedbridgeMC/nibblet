package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class NbtIntArray implements NbtElement, NbtIntArrayView {
    private final ArrayList<Integer> backingList;
    private final NbtIntArrayView view;

    private NbtIntArray(@NotNull ArrayList<@NotNull Integer> backingList) {
        this.backingList = backingList;
        view = new NbtIntArrayView() {
            @Override
            public int length() {
                return NbtIntArray.this.length();
            }

            @Override
            public int get(int i) {
                return NbtIntArray.this.get(i);
            }
        };
    }

    private NbtIntArray() {
        this(new ArrayList<>());
    }

    private NbtIntArray(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    public static @NotNull NbtIntArray create() {
        return new NbtIntArray();
    }

    public static @NotNull NbtIntArray create(int initialCapacity) {
        return new NbtIntArray(initialCapacity);
    }

    public static @NotNull NbtIntArray copyOf(int @NotNull ... values) {
        ArrayList<Integer> list = new ArrayList<>(values.length);
        for (int v : values)
            list.add(v);
        return new NbtIntArray(list);
    }

    @Override
    public @NotNull NbtIntArrayView view() {
        return view;
    }

    @Override
    public int length() {
        return backingList.size();
    }

    @Override
    public int get(int i) {
        return backingList.get(i);
    }

    public int set(int i, int v) {
        return backingList.set(i, v);
    }

    public boolean add(int v) {
        return backingList.add(v);
    }

    public int removeAt(int i) {
        return backingList.remove(i);
    }

    public boolean remove(int v) {
        return backingList.remove((Integer) v);
    }

    @Override
    public @NotNull NbtIntArray copy() {
        return new NbtIntArray(new ArrayList<>(backingList));
    }
}
