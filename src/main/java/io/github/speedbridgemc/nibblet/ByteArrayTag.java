package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class ByteArrayTag implements Tag, ByteArrayTagView {
    private final ArrayList<Byte> backingList;
    private final ByteArrayTagView view;

    private ByteArrayTag(ArrayList<Byte> backingList) {
        this.backingList = backingList;
        view = new ByteArrayTagView() {
            @Override
            public int length() {
                return ByteArrayTag.this.length();
            }

            @Override
            public byte get(int i) {
                return ByteArrayTag.this.get(i);
            }
        };
    }

    private ByteArrayTag() {
        this(new ArrayList<>());
    }

    private ByteArrayTag(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    public static @NotNull ByteArrayTag create() {
        return new ByteArrayTag();
    }

    public static @NotNull ByteArrayTag create(int initialCapacity) {
        return new ByteArrayTag(initialCapacity);
    }

    public static @NotNull ByteArrayTag copyOf(byte @NotNull ... values) {
        ArrayList<Byte> backingList = new ArrayList<>(values.length);
        for (byte value : values)
            backingList.add(value);
        return new ByteArrayTag(backingList);
    }

    @Override
    public @NotNull ByteArrayTagView view() {
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
    public @NotNull ByteArrayTag copy() {
        return new ByteArrayTag(new ArrayList<>(backingList));
    }
}
