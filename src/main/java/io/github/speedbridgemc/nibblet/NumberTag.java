package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a tag that encodes a number value.
 */
public interface NumberTag extends Tag {
    /**
     * Gets the value encoded by this tag as a {@link Number}.
     * @return value as number
     */
    @NotNull Number valueAsNumber();
}
