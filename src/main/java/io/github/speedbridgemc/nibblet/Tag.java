package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

/**
 * The base interface for all <em>tags</em> - in-memory representations of NBT structures.
 */
public interface Tag {
    /**
     * Gets this tag's type.
     * @return tag type
     */
    @NotNull TagType type();

    /**
     * Creates a shallow copy of this tag.
     *
     * @implNote Implementations may simply return {@code this} if the tag is immutable.
     *
     * @return shallow copy of tag
     */
    @NotNull Tag copy();

    /**
     * Creates a deep copy of this tag.
     *
     * @implNote Implementations may simply return {@code this} if the tag is immutable.
     *
     * @return deep copy of tag
     */
    default @NotNull Tag deepCopy() {
        return copy();
    }

    /**
     * Gets an immutable view of this tag.
     *
     * @implNote Implementations may simply return {@code this} if the tag is immutable.
     *
     * @return immutable view of tag
     */
    default @NotNull Tag view() {
        return this;
    }
}
