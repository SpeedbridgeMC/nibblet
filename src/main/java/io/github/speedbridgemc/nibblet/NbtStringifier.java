package io.github.speedbridgemc.nibblet;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * Provides utility methods for representing NBT structures as strings.
 */
public final class NbtStringifier {
    private NbtStringifier() { }

    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DECIMAL_FORMAT = new DecimalFormat();
        DECIMAL_FORMAT.setMaximumFractionDigits(340); // DecimalFormat.DOUBLE_FRACTION_DIGITS
    }

    /**
     * Prints an NBT structure using <a href="https://wiki.vg/NBT">wiki.vg</a>'s format to {@link System#out}.
     * @param name name of root element
     * @param nbt root element
     */
    public static void printWikiVGString(@NotNull String name, @NotNull NbtElement nbt) {
        printWikiVGString(name, nbt, System.out);
    }

    /**
     * Prints an NBT structure using <a href="https://wiki.vg/NBT">wiki.vg</a>'s format.
     * @param name name of root element
     * @param nbt root element
     * @param out output stream
     */
    public static void printWikiVGString(@NotNull String name, @NotNull NbtElement nbt, @NotNull PrintStream out) {
        printWikiVGString(name, nbt, out, "");
    }

    private static void printWikiVGString(@NotNull String name, @NotNull NbtElement nbt, @NotNull PrintStream out,
                                          @NotNull String indent) {
        out.format("%s%s(%s): ", indent, nbt.type(), name.isEmpty() ? "None" : "'" + name + "'");
        switch (nbt.type()) {
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
            out.format("%d%n", ((NbtNumber) nbt).valueAsNumber().longValue());
            break;
        case FLOAT:
        case DOUBLE:
            out.format("%s%n", DECIMAL_FORMAT.format(((NbtNumber) nbt).valueAsNumber().doubleValue()));
            break;
        case BYTE_ARRAY:
            NbtByteArrayView nbtByteArr = (NbtByteArrayView) nbt;
            out.print("[");
            for (int i = 0, length = nbtByteArr.length(); i < length; i++) {
                out.format("%d", nbtByteArr.get(i));
                if (i < length - 1)
                    out.print(", ");
            }
            out.println("]");
            break;
        case STRING:
            out.format("'%s'%n", ((NbtString) nbt).value());
            break;
        case LIST:
            NbtListView nbtList = (NbtListView) nbt;
            out.format("%s%n%s{%n", entryCount(nbtList.size()), indent);
            for (NbtElement item : nbtList)
                printWikiVGString("", item, out, indent + "  ");
            out.format("%s}%n", indent);
            break;
        case OBJECT:
            NbtObjectView nbtObj = (NbtObjectView) nbt;
            out.format("%s%n%s{%n", entryCount(nbtObj.size()), indent);
            for (NbtObjectView.Entry entry : nbtObj.entries())
                printWikiVGString(entry.name(), entry.element(), out, indent + "  ");
            out.format("%s}%n", indent);
            break;
        case INT_ARRAY:
            NbtIntArrayView nbtIntArr = (NbtIntArrayView) nbt;
            out.print("[");
            for (int i = 0, length = nbtIntArr.length(); i < length; i++) {
                out.format("%d", nbtIntArr.get(i));
                if (i < length - 1)
                    out.print(", ");
            }
            out.println("]");
            break;
        case LONG_ARRAY:
            NbtLongArrayView nbtLongArr = (NbtLongArrayView) nbt;
            out.print("[");
            for (int i = 0, length = nbtLongArr.length(); i < length; i++) {
                out.format("%d", nbtLongArr.get(i));
                if (i < length - 1)
                    out.print(", ");
            }
            out.println("]");
            break;
        default:
            throw new RuntimeException("Unprintable NBT element type " + nbt.type());
        }
    }

    /**
     * Converts an NBT structure to a string using <a href="https://wiki.vg/NBT">wiki.vg</a>'s format.
     * @param name name of root element
     * @param nbt root element
     */
    public static @NotNull String toWikiVGString(@NotNull String name, @NotNull NbtElement nbt) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos)) {
            printWikiVGString(name, nbt, ps, "");
        }
        try {
            return baos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is unsupported?!", e);
        }
    }

    private static @NotNull String entryCount(int entries) {
        return entries == 1 ? "1 entry" : entries + " entries";
    }
}
