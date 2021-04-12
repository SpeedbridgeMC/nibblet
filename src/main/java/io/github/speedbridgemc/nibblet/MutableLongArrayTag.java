package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class MutableLongArrayTag extends LongArrayTag {
    private final ArrayList<Long> backingList;

    private MutableLongArrayTag() {
        super(null);
        backingList = new ArrayList<>();
    }

    private MutableLongArrayTag(int initialCapacity) {
        super(null);
        backingList = new ArrayList<>(initialCapacity);
    }

    private MutableLongArrayTag(@NotNull ArrayList<@NotNull Long> backingList) {
        super(null);
        this.backingList = backingList;
    }

    public static @NotNull MutableLongArrayTag create() {
        return new MutableLongArrayTag();
    }

    public static @NotNull MutableLongArrayTag create(int initialCapacity) {
        return new MutableLongArrayTag(initialCapacity);
    }

    public static @NotNull MutableLongArrayTag copyOf(long @NotNull ... values) {
        ArrayList<Long> list = new ArrayList<>(values.length);
        for (long v : values)
            list.add(v);
        return new MutableLongArrayTag(list);
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
    public long @NotNull [] toArray() {
        long[] array = new long[length()];
        for (int i = 0; i < array.length; i++)
            array[i] = get(i);
        return array;
    }

    @Override
    public @NotNull MutableLongArrayTag copy() {
        return new MutableLongArrayTag(new ArrayList<>(backingList));
    }

}
