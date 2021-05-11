package io.github.speedbridgemc.nibblet.stream;

import java.io.IOException;

/**
 * Thrown to indicate that an NBT structure is malformed.
 */
public final class MalformedNbtDataException extends IOException {
    /**
     * Constructs a {@code MalformedTagException} with the specified detail message.
     * @param message the detail message
     */
    public MalformedNbtDataException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code MalformedTagException} with the specified detail message
     * and cause.
     * @param message the detail message
     * @param cause the cause, can be null
     */
    public MalformedNbtDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
