package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an NBT element that encodes a number value.
 */
public interface NbtNumber extends NbtElement {
    /**
     * Gets the value encoded by this tag as a {@link Number}.
     * @return value as number
     */
    @NotNull Number valueAsNumber();
}
