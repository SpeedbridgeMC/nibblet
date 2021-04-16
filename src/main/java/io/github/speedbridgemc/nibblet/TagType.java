package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Represents a tag type.
 */
public enum TagType {
    /**
     * {@code TAG_End} - End of a {@link #COMPOUND} tag. Also used for the item type of empty {@link #LIST}s.
     */
    END((byte) 0, "TAG_End"),
    /**
     * {@code TAG_Byte} - Encodes a {@code byte} value.
     *
     * @see ByteTag
     */
    BYTE((byte) 1, "TAG_Byte"),
    /**
     * {@code TAG_Short} - Encodes a {@code short} value.
     *
     * @see ShortTag
     */
    SHORT((byte) 2, "TAG_Short"),
    /**
     * {@code TAG_Int} - Encodes an {@code int} value.
     *
     * @see IntTag
     */
    INT((byte) 3, "TAG_Int"),
    /**
     * {@code TAG_Long} - Encodes a {@code long} value.
     *
     * @see LongTag
     */
    LONG((byte) 4, "TAG_Long"),
    /**
     * {@code TAG_Float} - Encodes a {@code float} value.
     *
     * @see FloatTag
     */
    FLOAT((byte) 5, "TAG_Float"),
    /**
     * {@code TAG_Double} - Encodes a {@code double} value.
     *
     * @see DoubleTag
     */
    DOUBLE((byte) 6, "TAG_Double"),
    /**
     * {@code TAG_Byte_Array} - Encodes an array of {@code byte} values.
     *
     * @see ByteArrayTag
     */
    BYTE_ARRAY((byte) 7, "TAG_Byte_Array"),
    /**
     * {@code TAG_String} - Encodes a {@link String} value.
     *
     * @see StringTag
     */
    STRING((byte) 8, "TAG_String"),
    /**
     * {@code TAG_List} - Encodes a list of tags.
     * 
     * @see ListTag
     */
    LIST((byte) 9, "TAG_List"),
    /**
     * {@code TAG_Compound} - Encodes a list of named tags.
     * 
     * @see CompoundTag
     */
    COMPOUND((byte) 10, "TAG_Compound"),
    /**
     * {@code TAG_Int_Array} - Encodes an array of {@code int} values.
     *
     * @see IntArrayTag
     */
    INT_ARRAY((byte) 11, "TAG_Int_Array"),
    /**
     * {@code TAG_Long_Array} - Encodes an array of {@code long} values.
     *
     * @see LongArrayTag
     */
    LONG_ARRAY((byte) 12, "TAG_Long_Array"),
    /**
     * A pseudo-tag type representing a root {@code TAG_List} (Bedrock Edition supports this).<p>
     * Notably, this list doesn't have its size specified - it always contains a single entry.
     */
    ROOT_LIST((byte) 99, "TAG_Root_List");

    private final byte id;
    private final String name;

    TagType(byte id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the numeric ID of this tag type.
     * @return type ID
     */
    public byte id() {
        return id;
    }

    /**
     * Gets the name of this tag type.
     * @return type name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if this tag type encodes a number.
     * @return {@code true} if number, {@code false} otherwise
     */
    public boolean isNumber() {
        return this == BYTE || this == SHORT || this == INT || this == LONG || this == FLOAT || this == DOUBLE;
    }

    private static final HashMap<Byte, TagType> MAP;

    static {
        MAP = new HashMap<>();
        for (TagType type : values()) {
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
    public static @Nullable TagType fromId(byte id) {
        return MAP.get(id);
    }
}
