package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an NBT element that encodes a number value.
 */
public interface NbtNumber extends NbtElement {
    /**
     * Gets the value encoded by this tag as a {@link Number}.
     * @return value as number
     */
    @NotNull Number valueAsNumber();

    @Override
    default boolean equals(Object obj) {
        if (obj instanceof NbtNumber)
            return valueAsNumber().equals(((NbtNumber) obj).valueAsNumber());
        return false;
    }

    @Override
    default int hashCode() {
        return Objects.hash(type(), valueAsNumber());
    }
}
