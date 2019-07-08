package com.opengg.core.network.common;

public class PacketType {
    public static final byte
            SERVER_UPDATE = 0,
            CLIENT_ACTION_UPDATE = 0,
            ACK = 127,
            SERVER_COMPONENT_CREATE = 1,
            SERVER_COMPONENT_REMOVE = 2,
            BULK_DATA_INIT = 5,
            BULK_DATA_CHUNK = 6;
}
