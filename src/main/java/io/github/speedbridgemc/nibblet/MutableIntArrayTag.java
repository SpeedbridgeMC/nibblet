package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class MutableIntArrayTag extends IntArrayTag {
    private final ArrayList<Integer> backingList;

    private MutableIntArrayTag() {
        super(null);
        backingList = new ArrayList<>();
    }

    private MutableIntArrayTag(int initialCapacity) {
        super(null);
        backingList = new ArrayList<>(initialCapacity);
    }

    private MutableIntArrayTag(@NotNull ArrayList<@NotNull Integer> backingList) {
        super(null);
        this.backingList = backingList;
    }

    public static @NotNull MutableIntArrayTag create() {
        return new MutableIntArrayTag();
    }

    public static @NotNull MutableIntArrayTag create(int initialCapacity) {
        return new MutableIntArrayTag(initialCapacity);
    }

    public static @NotNull MutableIntArrayTag copyOf(int @NotNull ... values) {
        ArrayList<Integer> list = new ArrayList<>(values.length);
        for (int v : values)
            list.add(v);
        return new MutableIntArrayTag(list);
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
    public int @NotNull [] toArray() {
        int[] array = new int[length()];
        for (int i = 0; i < array.length; i++)
            array[i] = get(i);
        return array;
    }

    @Override
    public @NotNull MutableIntArrayTag copy() {
        return new MutableIntArrayTag(backingList);
    }
}
