package io.github.speedbridgemc.nibblet.stream;

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
}
