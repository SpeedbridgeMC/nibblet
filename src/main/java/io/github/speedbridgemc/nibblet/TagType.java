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
    END((byte) 0),
    /**
     * {@code TAG_Byte} - Encodes a {@code byte} value.
     *
     * @see ByteTag
     */
    BYTE((byte) 1),
    /**
     * {@code TAG_Short} - Encodes a {@code short} value.
     *
     * @see ShortTag
     */
    SHORT((byte) 2),
    /**
     * {@code TAG_Int} - Encodes an {@code int} value.
     *
     * @see IntTag
     */
    INT((byte) 3),
    /**
     * {@code TAG_Long} - Encodes a {@code long} value.
     *
     * @see LongTag
     */
    LONG((byte) 4),
    /**
     * {@code TAG_Float} - Encodes a {@code float} value.
     *
     * @see FloatTag
     */
    FLOAT((byte) 5),
    /**
     * {@code TAG_Double} - Encodes a {@code double} value.
     *
     * @see DoubleTag
     */
    DOUBLE((byte) 6),
    /**
     * {@code TAG_Byte_Array} - Encodes an array of {@code byte} values.
     *
     * @see ByteArrayTag
     * @see MutableByteArrayTag
     */
    BYTE_ARRAY((byte) 7),
    /**
     * {@code TAG_String} - Encodes a {@link String} value.
     *
     * @see StringTag
     */
    STRING((byte) 8),
    /**
     * {@code TAG_List} - Encodes a list of tags.
     * 
     * @see ListTag
     * @see MutableListTag
     */
    LIST((byte) 9),
    /**
     * {@code TAG_Compound} - Encodes a list of named tags.
     * 
     * @see CompoundTag
     * @see MutableCompoundTag
     */
    COMPOUND((byte) 10),
    /**
     * {@code TAG_Int_Array} - Encodes an array of {@code int} values.
     *
     * @see IntArrayTag
     * @see MutableIntArrayTag
     */
    INT_ARRAY((byte) 11),
    /**
     * {@code TAG_Long_Array} - Encodes an array of {@code long} values.
     *
     * @see LongArrayTag
     * @see MutableLongArrayTag
     */
    LONG_ARRAY((byte) 12),
    /**
     * A pseudo-tag type, used in {@link CompoundTag#contains(String, TagType)} to check if the compound tag contains
     * a tag that encodes a number.
     */
    NUMBER((byte) -1);

    private final byte id;

    TagType(byte id) {
        this.id = id;
    }

    /**
     * Gets the numeric ID of this tag type.
     * @return type ID
     */
    public byte id() {
        return id;
    }

    /**
     * Checks if this tag type encodes a number.
     * @return {@code true} if number, {@code false} otherwise
     */
    public boolean isNumber() {
        return this == BYTE || this == SHORT || this == INT || this == LONG || this == FLOAT || this == DOUBLE || this == NUMBER;
    }

    private static final HashMap<Byte, TagType> MAP;

    static {
        MAP = new HashMap<>();
        for (TagType type : values())
            MAP.put(type.id(), type);
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
