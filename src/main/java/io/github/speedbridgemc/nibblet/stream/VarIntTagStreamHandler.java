package io.github.speedbridgemc.nibblet.stream;

import java.nio.ByteOrder;

public class VarIntTagStreamHandler extends StandardTagStreamHandler {
    public VarIntTagStreamHandler() {
        super(ByteOrder.LITTLE_ENDIAN);
    }

    // TODO
}
