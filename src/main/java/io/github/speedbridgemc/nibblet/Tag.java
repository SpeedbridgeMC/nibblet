package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

/**
 * Base interface for all tags.
 */
public interface Tag extends Cloneable {
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
     * Creates a deep mutable copy of this tag.
     *
     * @implNote Implementations may simply return {@code this} if the tag is immutable.
     *
     * @return deep mutable copy of tag
     */
    default @NotNull Tag mutableCopy() {
        return deepCopy();
    }
}
