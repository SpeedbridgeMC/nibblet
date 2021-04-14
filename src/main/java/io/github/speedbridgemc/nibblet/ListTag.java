package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class ListTag implements Tag, ListTagView {
    public static final class Builder {
        private final ArrayList<Tag> backingList;
        private TagType itemType;

        private Builder() {
            backingList = new ArrayList<>();
            itemType = TagType.END;
        }

        private Builder(int initialCapacity) {
            backingList = new ArrayList<>(initialCapacity);
            itemType = TagType.END;
        }

        public @NotNull Builder add(@NotNull Tag v) {
            if (itemType == TagType.END)
                itemType = v.type();
            else if (itemType != v.type())
                throw new IllegalArgumentException("Tried to add tag of type " + v.type() + " to list of type " + itemType + "!");
            backingList.add(v);
            return this;
        }

        public @NotNull ListTag build() {
            return new ListTag(itemType, new ArrayList<>(backingList));
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    private final List<Tag> backingList;
    private TagType itemType;
    private final ListTagView view;

    private ListTag(@NotNull TagType itemType, @NotNull List<@NotNull Tag> backingList) {
        this.backingList = backingList;
        this.itemType = itemType;
        view = new ListTagView() {
            @Override
            public @NotNull TagType itemType() {
                return ListTag.this.itemType();
            }

            @Override
            public int size() {
                return ListTag.this.size();
            }

            @Override
            public @NotNull Tag get(int i) {
                return ListTag.this.get(i).view();
            }

            @Override
            public @NotNull Iterator<Tag> iterator() {
                return ListTag.this.iterator();
            }
        };
    }

    public static @NotNull ListTag create() {
        return new ListTag(TagType.END, new ArrayList<>());
    }

    public static @NotNull ListTag create(int initialCapacity) {
        return new ListTag(TagType.END, new ArrayList<>(initialCapacity));
    }

    @SafeVarargs
    public static <T extends Tag> @NotNull ListTag of(@NotNull T @NotNull ... values) {
        Builder builder = builder(values.length);
        for (T value : values)
            builder.add(value);
        return builder.build();
    }

    public static <T extends Tag> @NotNull ListTag copyOf(@NotNull Iterable<@NotNull T> values) {
        Builder builder = builder();
        for (Tag v : values)
            builder.add(v);
        return builder.build();
    }

    @Override
    public @NotNull TagType type() {
        return TagType.LIST;
    }

    @Override
    public @NotNull ListTagView view() {
        return view;
    }

    @Override
    public @NotNull TagType itemType() {
        return itemType;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public @NotNull Tag get(int i) {
        return backingList.get(i);
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

    @NotNull
    @Override
    public Iterator<Tag> iterator() {
        return new Iterator<Tag>() {
            private final Iterator<Tag> delegate = backingList.iterator();

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public Tag next() {
                return delegate.next();
            }

            @Override
            public void forEachRemaining(Consumer<? super Tag> action) {
                delegate.forEachRemaining(action);
            }
        };
    }

    @Override
    public @NotNull ListTag copy() {
        return new ListTag(itemType, backingList);
    }

    @Override
    public @NotNull ListTag deepCopy() {
        ListTag.Builder builder = builder(backingList.size());
        for (Tag tag : this)
            builder.add(tag);
        return builder.build();
    }
}
