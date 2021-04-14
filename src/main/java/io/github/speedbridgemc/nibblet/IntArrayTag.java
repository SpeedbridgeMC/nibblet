package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class IntArrayTag implements Tag, IntArrayTagView {
    private final ArrayList<Integer> backingList;
    private final IntArrayTagView view;

    private IntArrayTag(@NotNull ArrayList<@NotNull Integer> backingList) {
        this.backingList = backingList;
        view = new IntArrayTagView() {
            @Override
            public int length() {
                return IntArrayTag.this.length();
            }

            @Override
            public int get(int i) {
                return IntArrayTag.this.get(i);
            }
        };
    }

    private IntArrayTag() {
        this(new ArrayList<>());
    }

    private IntArrayTag(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    public static @NotNull IntArrayTag create() {
        return new IntArrayTag();
    }

    public static @NotNull IntArrayTag create(int initialCapacity) {
        return new IntArrayTag(initialCapacity);
    }

    public static @NotNull IntArrayTag copyOf(int @NotNull ... values) {
        ArrayList<Integer> list = new ArrayList<>(values.length);
        for (int v : values)
            list.add(v);
        return new IntArrayTag(list);
    }

    @Override
    public @NotNull IntArrayTagView view() {
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
    public @NotNull IntArrayTag copy() {
        return new IntArrayTag(new ArrayList<>(backingList));
    }
}
