package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class MutableByteArrayTag extends ByteArrayTag {
    private final ArrayList<Byte> backingList;

    private MutableByteArrayTag() {
        super(null);
        backingList = new ArrayList<>();
    }

    private MutableByteArrayTag(int initialCapacity) {
        super(null);
        backingList = new ArrayList<>(initialCapacity);
    }

    private MutableByteArrayTag(@NotNull ArrayList<@NotNull Byte> backingList) {
        super(null);
        this.backingList = backingList;
    }

    public static @NotNull MutableByteArrayTag create() {
        return new MutableByteArrayTag();
    }

    public static @NotNull MutableByteArrayTag create(int initialCapacity) {
        return new MutableByteArrayTag(initialCapacity);
    }

    public static @NotNull MutableByteArrayTag copyOf(byte @NotNull ... values) {
        ArrayList<Byte> list = new ArrayList<>(values.length);
        for (byte v : values)
            list.add(v);
        return new MutableByteArrayTag(list);
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
    public byte @NotNull [] toArray() {
        byte[] array = new byte[length()];
        for (int i = 0; i < array.length; i++)
            array[i] = get(i);
        return array;
    }

    @Override
    public @NotNull MutableByteArrayTag copy() {
        return new MutableByteArrayTag(new ArrayList<>(backingList));
    }

}
