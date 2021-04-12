package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class MutableListTag extends ListTag {
    private MutableListTag() {
        super(TagType.END, new ArrayList<>());
    }

    public static @NotNull MutableListTag create() {
        return new MutableListTag();
    }

    public static <T extends Tag> @NotNull MutableListTag copyOf(@NotNull Iterable<@NotNull T> values) {
        MutableListTag tag = create();
        tag.addAll(values);
        return tag;
    }

    private void checkTagType(@NotNull Tag tag) {
        if (itemType == TagType.END)
            itemType = tag.type();
        else if (itemType != tag.type())
            throw new IllegalArgumentException("Tried to add tag of type " + tag.type() + " to list of type " + itemType + "!");
    }

    public @NotNull Tag set(int i, @NotNull Tag v) {
        checkTagType(v);
        return backingList.set(i, v);
    }

    public boolean add(@NotNull Tag v) {
        checkTagType(v);
        return backingList.add(v);
    }

    public @NotNull Tag removeAt(int i) {
        return backingList.remove(i);
    }

    public boolean remove(@NotNull Tag v) {
        return backingList.remove(v);
    }

    public <T extends Tag> boolean addAll(@NotNull Iterable<@NotNull T> values) {
        boolean changed = false;
        for (Tag v : values)
            changed |= add(v);
        return changed;
    }

    @Override
    public @NotNull MutableListTag copy() {
        MutableListTag tag = new MutableListTag();
        tag.backingList.addAll(backingList);
        tag.itemType = itemType;
        return tag;
    }

    @Override
    public @NotNull MutableListTag deepCopy() {
        MutableListTag tag = new MutableListTag();
        for (Tag v : backingList)
            tag.backingList.add(v.deepCopy());
        tag.itemType = itemType;
        return tag;
    }

}
