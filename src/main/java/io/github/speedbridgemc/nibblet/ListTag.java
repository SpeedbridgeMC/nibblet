package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ListTag implements Tag, Iterable<Tag> {
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

    protected final List<Tag> backingList;
    protected TagType itemType;

    ListTag(@NotNull TagType itemType, @NotNull List<@NotNull Tag> backingList) {
        this.backingList = backingList;
        this.itemType = itemType;
    }

    @Override
    public @NotNull TagType type() {
        return TagType.LIST;
    }

    public @NotNull TagType itemType() {
        return itemType;
    }

    public int size() {
        return backingList.size();
    }

    public @NotNull Tag get(int i) {
        return backingList.get(i);
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
        return this;
    }

    @Override
    public @NotNull MutableListTag mutableCopy() {
        MutableListTag tag = MutableListTag.create();
        tag.itemType = itemType;
        for (Tag v : backingList)
            tag.backingList.add(v.mutableCopy());
        return tag;
    }
}
