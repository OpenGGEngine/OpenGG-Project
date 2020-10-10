package com.opengg.core.network.common;

import com.opengg.core.console.GGConsole;
import com.opengg.core.util.GGInputStream;

import java.io.IOException;
import java.util.Arrays;

public class ReceivingBulkMessage {
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

    private int packetAmountReceived = 0;
    private byte[] dataCache;

    public ReceivingBulkMessage(Packet initialPacket) throws IOException {
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

        packetAmountReceived++;
        if(isComplete()){
            GGConsole.debug("Fully received bulk message");
            synchronized (RECEPTION_LOCK){
                RECEPTION_LOCK.notifyAll();
            }
        }
    }

    public byte[] getAllData(){
        synchronized (RECEPTION_LOCK){
            if(!isComplete()){
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

    public int getSize() {
        return size;
    }

    public long getBytesReceived(){
        return Arrays.stream(data).mapToLong(c -> c.length).sum();
    }

    public int getPacketAmount() {
        return packetAmount;
    }

    public int getPacketAmountReceived() {
        return packetAmountReceived;
    }

    public boolean isComplete(){
        return packetAmountReceived == packetAmount;
    }

    private static class BulkMessagePacket{
        int id;
        int length;
        byte[] data;


        public BulkMessagePacket(Packet packet) throws IOException {
            var in = new GGInputStream(packet.getData());
            in.readLong(); //message identifier, useless
            id = in.readInt();
            length = in.readInt();
            data = in.readByteArray(length);
        }
    }
}
