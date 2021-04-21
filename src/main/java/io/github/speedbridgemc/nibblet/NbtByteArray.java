package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class NbtByteArray implements NbtElement, NbtByteArrayView {
    private final ArrayList<Byte> backingList;
    private final NbtByteArrayView view;

    private NbtByteArray(ArrayList<Byte> backingList) {
        this.backingList = backingList;
        view = new NbtByteArrayView() {
            @Override
            public int length() {
                return NbtByteArray.this.length();
            }

            @Override
            public byte get(int i) {
                return NbtByteArray.this.get(i);
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
}
