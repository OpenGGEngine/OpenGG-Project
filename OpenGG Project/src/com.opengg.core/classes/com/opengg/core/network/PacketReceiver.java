package com.opengg.core.network;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Tuple;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.world.WorldEngine;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketReceiver implements Runnable{
    private final List<Tuple<Byte, PacketProcessor>> processors;
    private final DatagramSocket socket;
    private int packetsize;

    public PacketReceiver(DatagramSocket socket, int packetsize){
        processors = new ArrayList<>();
        this.socket = socket;
        this.packetsize = packetsize;
    }

    public void addProccesor(byte type, PacketProcessor processor){
        processors.add(new Tuple<>(type, processor));
    }

    public int getPacketSize(){
        return packetsize;
    }

    public void setPacketSize(int size){
        this.packetsize = size;
    }

    public void start(){
        new Thread(this).start();
    }

    @Override
    public void run(){
        while(NetworkEngine.running() && !OpenGG.getEnded()){
            Packet packet = Packet.receive(socket, packetsize);
            byte[] bytes = packet.getData();
            byte type = packet.getData()[0];
            for(var processortuple : processors){
                if(processortuple.x == type)
                    processortuple.y.onPacketReceive(packet);
            }
        }
    }

    public interface PacketProcessor{
        void onPacketReceive(Packet packet);
    }

}
