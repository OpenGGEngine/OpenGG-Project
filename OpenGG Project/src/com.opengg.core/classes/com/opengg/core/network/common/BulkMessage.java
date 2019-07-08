package com.opengg.core.network.common;

import com.opengg.core.util.ArrayUtil;
import com.opengg.core.util.GGInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BulkMessage {
    private final Object RECEPTION_LOCK = new Object();

    private final ConnectionData source;
    private final String name;
    private final String path;
    private final boolean isFile;
    private final long id;
    private final int size;
    private final long hash;
    private final int packetAmount;
    private final BulkMessagePacket[] data;

    private int amountReceived = 0;
    private byte[] dataCache;

    public BulkMessage(Packet initialPacket) throws IOException {
        var in = new GGInputStream(initialPacket.getData());
        source = initialPacket.getConnection();
        id = in.readLong();
        size = in.readInt();
        packetAmount = in.readInt();
        isFile = in.readBoolean();
        hash = in.readLong();
        name = in.readString();
        if(isFile){
            path = in.readString();
        }else {
            path = "";
        }

        data = new BulkMessagePacket[packetAmount];
    }

    public void acceptPacket(Packet packet) throws IOException {
        var bulkPacket = new BulkMessagePacket(packet);
        data[bulkPacket.id] = bulkPacket;

        amountReceived++;
        if(amountReceived == packetAmount){
            synchronized (RECEPTION_LOCK){
                RECEPTION_LOCK.notifyAll();
            }
        }
    }

    public byte[] getAllData(){
        synchronized (RECEPTION_LOCK){
            if(getPacketAmount() != getAmountReceived()){
                try {
                    RECEPTION_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if(dataCache != null) return dataCache;

        var finalBuffer = new byte[size];

        int ptr = 0;
        for(var packet : data){
            var bytes = packet.data;
            for(var bytee : bytes){
                finalBuffer[ptr] = bytee;
                ptr++;
            }
        }

        dataCache = finalBuffer;
        return finalBuffer;
    }

    public ConnectionData getSource() {
        return source;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isFile() {
        return isFile;
    }

    public long getSize() {
        return size;
    }

    public long getBytesReceived(){
        return Arrays.stream(data).mapToLong(c -> c.length).sum();
    }

    public int getPacketAmount() {
        return packetAmount;
    }

    public int getAmountReceived() {
        return amountReceived;
    }

    public static class BulkMessagePacket{
        int id;
        int length;
        byte[] data;


        public BulkMessagePacket(Packet packet) throws IOException {
            var in = new GGInputStream(packet.getData());
            id = in.readInt();
            length = in.readInt();
            data = in.readByteArray(length);
        }
    }
}
