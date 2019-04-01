package com.opengg.core.network;

import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.util.Arrays;
import java.util.function.Consumer;

public interface PacketDataConsumer extends Consumer<Packet> {

    @Override
    default void accept(Packet packet) {
        acceptData(new GGInputStream(Arrays.copyOfRange(packet.getData(), 1, packet.getData().length)));
    }

    void acceptData(GGInputStream data);
}
