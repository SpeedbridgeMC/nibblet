package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

/**
 * The base interface for all <em>NBT elements</em> - in-memory representations of NBT structures.
 */
public interface NbtElement {
    /**
     * An empty array of {@code byte}s.
     */
    byte[] EMPTY_BYTE_ARRAY = new byte[0];
    /**
     * An empty array of {@code int}s.
     */
    int[] EMPTY_INT_ARRAY = new int[0];
    /**
     * An empty array of {@code long}s.
     */
    long[] EMPTY_LONG_ARRAY = new long[0];

    /**
     * Gets this tag's type.
     * @return tag type
     */
    @NotNull NbtType type();

    /**
     * Creates a shallow copy of this tag.
     *
     * @implNote Implementations may simply return {@code this} if the tag is immutable.
     *
     * @return shallow copy of tag
     */
    @NotNull NbtElement copy();

    /**
     * Creates a deep copy of this tag.
     *
     * @implNote Implementations may simply return {@code this} if the tag is immutable.
     *
     * @return deep copy of tag
     */
    default @NotNull NbtElement deepCopy() {
        return copy();
    }

    /**
     * Gets an immutable view of this tag.
     *
     * @implNote Implementations may simply return {@code this} if the tag is immutable.
     *
     * @return immutable view of tag
     */
    default @NotNull NbtElement view() {
        return this;
    }
}
