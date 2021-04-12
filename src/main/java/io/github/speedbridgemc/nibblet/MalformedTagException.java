package io.github.speedbridgemc.nibblet;

import java.io.IOException;

/**
 * Thrown to indicate that an NBT structure is malformed.
 */
public final class MalformedTagException extends IOException {
    /**
     * Constructs a {@code MalformedTagException} with no detail message.
     */
    public MalformedTagException() {
        super();
    }

    /**
     * Constructs a {@code MalformedTagException} with the specified detail message.
     * @param message the detail message
     */
    public MalformedTagException(String message) {
        super(message);
    }
}
