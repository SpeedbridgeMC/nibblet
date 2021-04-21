package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Represents an NBT element type.
 */
public enum NbtType {
    /**
     * {@code TAG_End} - End of a {@link #COMPOUND} element. Also used for the item type of empty {@link #LIST}s.
     */
    END((byte) 0, "TAG_End", 0),
    /**
     * {@code TAG_Byte} - Encodes a {@code byte} value.
     *
     * @see NbtByte
     */
    BYTE((byte) 1, "TAG_Byte", Byte.BYTES),
    /**
     * {@code TAG_Short} - Encodes a {@code short} value.
     *
     * @see NbtShort
     */
    SHORT((byte) 2, "TAG_Short", Short.BYTES),
    /**
     * {@code TAG_Int} - Encodes an {@code int} value.
     *
     * @see NbtInt
     */
    INT((byte) 3, "TAG_Int", Integer.BYTES),
    /**
     * {@code TAG_Long} - Encodes a {@code long} value.
     *
     * @see NbtLong
     */
    LONG((byte) 4, "TAG_Long", Long.BYTES),
    /**
     * {@code TAG_Float} - Encodes a {@code float} value.
     *
     * @see NbtFloat
     */
    FLOAT((byte) 5, "TAG_Float", Float.BYTES),
    /**
     * {@code TAG_Double} - Encodes a {@code double} value.
     *
     * @see NbtDouble
     */
    DOUBLE((byte) 6, "TAG_Double", Double.BYTES),
    /**
     * {@code TAG_Byte_Array} - Encodes an array of {@code byte} values.
     *
     * @see NbtByteArray
     */
    BYTE_ARRAY((byte) 7, "TAG_Byte_Array"),
    /**
     * {@code TAG_String} - Encodes a {@link String} value.
     *
     * @see NbtString
     */
    STRING((byte) 8, "TAG_String"),
    /**
     * {@code TAG_List} - Encodes a list of elements.
     * 
     * @see NbtList
     */
    LIST((byte) 9, "TAG_List"),
    /**
     * {@code TAG_Compound} - Encodes a list of named elements.
     * 
     * @see NbtObject
     */
    COMPOUND((byte) 10, "TAG_Compound"),
    /**
     * {@code TAG_Int_Array} - Encodes an array of {@code int} values.
     *
     * @see NbtIntArray
     */
    INT_ARRAY((byte) 11, "TAG_Int_Array"),
    /**
     * {@code TAG_Long_Array} - Encodes an array of {@code long} values.
     *
     * @see NbtLongArray
     */
    LONG_ARRAY((byte) 12, "TAG_Long_Array"),
    /**
     * A pseudo-element type representing a root {@code TAG_List} (Bedrock Edition supports this).<p>
     * Notably, this list doesn't have its size specified - it always contains a single entry.
     */
    ROOT_LIST((byte) 99, "TAG_Root_List");

    private final byte id;
    private final String name;
    private final long payloadSize;

    NbtType(byte id, String name, long payloadSize) {
        this.id = id;
        this.name = name;
        this.payloadSize = payloadSize;
    }

    NbtType(byte id, String name) {
        this(id, name, -1);
    }

    /**
     * Gets the numeric ID of this element type.
     * @return type ID
     */
    public byte id() {
        return id;
    }

    /**
     * Gets the name of this element type.
     * @return type name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if this element type's value has a constant size.
     * @return {@code true} if payload size is constant, {@code false} otherwise
     */
    public boolean hasConstantPayloadSize() {
        return payloadSize >= 0;
    }

    /**
     * Gets the size of this element type's encoded value in bytes. If the size is non-constant, returns -1 instead.
     * @return payload size, or -1 if non-constant
     */
    public long payloadSize() {
        return payloadSize;
    }

    /**
     * Checks if this element type encodes a number.
     * @return {@code true} if number, {@code false} otherwise
     */
    public boolean isNumber() {
        return this == BYTE || this == SHORT || this == INT || this == LONG || this == FLOAT || this == DOUBLE;
    }

    private static final HashMap<Byte, NbtType> MAP;

    static {
        MAP = new HashMap<>();
        for (NbtType type : values()) {
            if (type == ROOT_LIST)
                continue;
            MAP.put(type.id(), type);
        }
    }

    /**
     * Gets a {@code TagType} based on its ID.
     * @param id type ID
     * @return matching {@code TagType}, or {@code null} if no matching type was found
     */
    public static @Nullable NbtType fromId(byte id) {
        return MAP.get(id);
    }
}
