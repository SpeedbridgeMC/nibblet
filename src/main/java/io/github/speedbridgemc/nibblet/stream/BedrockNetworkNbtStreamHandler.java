package io.github.speedbridgemc.nibblet.stream;

import io.github.speedbridgemc.nibblet.NbtType;
import io.github.speedbridgemc.nibblet.util.VarInts;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

@ApiStatus.Internal
public final class BedrockNetworkNbtStreamHandler extends StandardNbtStreamHandler {
    public BedrockNetworkNbtStreamHandler() {
        super(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public long payloadSize(@NotNull NbtType type) {
        switch (type) {
        case BYTE:
            return Byte.BYTES;
        case SHORT:
            return Short.BYTES;
        case FLOAT:
            return Float.BYTES;
        case DOUBLE:
            return Double.BYTES;
        default:
            return -1;
        }
    }

    @Override
    public int readInt(@NotNull InputStream in) throws IOException {
        return VarInts.readVarIntZigZag(in);
    }

    @Override
    public long readLong(@NotNull InputStream in) throws IOException {
        return VarInts.readVarLongZigZag(in);
    }

    @Override
    public int readUTFLength(@NotNull InputStream in) throws IOException {
        return VarInts.readVarInt(in);
    }

    @Override
    public void writeInt(@NotNull OutputStream out, int value) throws IOException {
        VarInts.writeVarIntZigZag(out, value);
    }

    @Override
    public void writeLong(@NotNull OutputStream out, long value) throws IOException {
        VarInts.writeVarLongZigZag(out, value);
    }

    @Override
    public void writeUTFLength(@NotNull OutputStream out, int utflen) throws IOException {
        VarInts.writeVarInt(out, utflen);
    }

    @Override
    public void skipInt(@NotNull InputStream in) throws IOException {
        readInt(in);
    }

    @Override
    public void skipLong(@NotNull InputStream in) throws IOException {
        readLong(in);
    }
}
