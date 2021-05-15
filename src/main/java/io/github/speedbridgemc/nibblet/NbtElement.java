package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

/**
 * The base interface for all <em>NBT elements</em> - in-memory representations of NBT structures.
 */
public interface NbtElement {
    /**
     * Gets this NBT element's type.
     * @return NBT element type
     */
    @NotNull NbtType type();

    /**
     * Checks if this NBT element is of the given type.
     * @param type type to check
     * @return {@code true} if element is of type, {@code false} otherwise
     */
    default boolean isOf(@NotNull NbtType type) {
        return type == type();
    }

    /**
     * Creates a shallow copy of this NBT element.
     *
     * @implNote Implementations may simply return {@code this} if the NBT element is immutable.
     *
     * @return shallow copy of NBT element
     */
    @NotNull NbtElement copy();

    /**
     * Creates a deep copy of this NBT element.
     *
     * @implNote Implementations may simply return {@code this} if the NBT element is immutable.
     *
     * @return deep copy of NBT element
     */
    default @NotNull NbtElement deepCopy() {
        return copy();
    }

    /**
     * Gets an immutable view of this NBT element.
     *
     * @implNote Implementations may simply return {@code this} if the NBT element is immutable.
     *
     * @return immutable view of NBT element
     */
    default @NotNull NbtElement view() {
        return this;
    }

    /**
     * Checks if the specified object is equal to this NBT element.
     * @param obj object to check against
     * @return {@code true} if equal, {@code false} otherwise
     */
    boolean equals(Object obj);

    /**
     * Calculates this NBT element's hash code.
     * @return hash code
     */
    int hashCode();
}
