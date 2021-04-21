package io.github.speedbridgemc.nibblet;

import java.io.IOException;

/**
 * Thrown to indicate that an NBT structure is malformed.
 */
public final class MalformedNbtException extends IOException {
    /**
     * Constructs a {@code MalformedTagException} with no detail message.
     */
    public MalformedNbtException() {
        super();
    }

    /**
     * Constructs a {@code MalformedTagException} with the specified detail message.
     * @param message the detail message
     */
    public MalformedNbtException(String message) {
        super(message);
    }
}
