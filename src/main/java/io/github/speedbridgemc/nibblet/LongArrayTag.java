package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class LongArrayTag implements Tag, LongArrayTagView {
    private final ArrayList<Long> backingList;
    private final LongArrayTagView view;

    private LongArrayTag(@NotNull ArrayList<@NotNull Long> backingList) {
        this.backingList = backingList;
        view = new LongArrayTagView() {
            @Override
            public int length() {
                return LongArrayTag.this.length();
            }

            @Override
            public long get(int i) {
                return LongArrayTag.this.get(i);
            }
        };
    }

    private LongArrayTag() {
        this(new ArrayList<>()) ;
    }

    private LongArrayTag(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    public static @NotNull LongArrayTag create() {
        return new LongArrayTag();
    }

    public static @NotNull LongArrayTag create(int initialCapacity) {
        return new LongArrayTag(initialCapacity);
    }

    public static @NotNull LongArrayTag copyOf(long @NotNull ... values) {
        ArrayList<Long> list = new ArrayList<>(values.length);
        for (long v : values)
            list.add(v);
        return new LongArrayTag(list);
    }

    @Override
    public @NotNull LongArrayTagView view() {
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
    public @NotNull LongArrayTag copy() {
        return new LongArrayTag(new ArrayList<>(backingList));
    }

}
