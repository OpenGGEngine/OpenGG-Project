package com.opengg.core.network;

import com.opengg.core.math.Tuple;
import com.opengg.core.network.common.BulkMessage;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.util.GGFuture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class BulkNetworkDataManager {
    public static long maxAllowedBandwidth;
    public static Map<Tuple<ConnectionData, Long>, BulkMessage> currentReceivedMessages = new HashMap<>();
    //public static ListP


    public static void initialize(){
        NetworkEngine.getPacketReceiver().addProcessor(PacketType.BULK_DATA_INIT, (p) -> {
            try {
                var message = new BulkMessage(p);
                currentReceivedMessages.put(Tuple.of(message.getSource(), message.getId()), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        NetworkEngine.getPacketReceiver().addProcessor(PacketType.BULK_DATA_CHUNK, (p) -> {

        });
    }

    public static void send(ByteBuffer data){

    }

    public static GGFuture<ByteBuffer> receive(){
return null;
    }

}
