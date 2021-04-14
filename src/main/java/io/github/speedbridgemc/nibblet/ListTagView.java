package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface ListTagView extends Tag, Iterable<Tag> {
    @Override
    default @NotNull TagType type() {
        return TagType.LIST;
    }

    @NotNull TagType itemType();
    int size();
    @NotNull Tag get(int i);

    @Override
    @NotNull Iterator<Tag> iterator();

    @Override
    default @NotNull ListTagView view() {
        return this;
    }

    @Override
    default @NotNull ListTagView copy() {
        return this;
    }
}
