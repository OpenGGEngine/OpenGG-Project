package com.opengg.core.network;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Tuple;
import com.opengg.core.thread.ThreadManager;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class PacketReceiver implements Runnable{
    private final List<Tuple<Byte, PacketAcceptor>> processors; //since im definitely going to forget why the tuple contains a byte its a packet identifier
    private final DatagramSocket socket;
    private int packetsize;

    public PacketReceiver(DatagramSocket socket, int packetsize){
        processors = new ArrayList<>();
        this.socket = socket;
        this.packetsize = packetsize;
    }

    public void addProcessor(byte type, PacketAcceptor processor){
        processors.add(new Tuple<>(type, processor));
    }

    public int getPacketSize(){
        return packetsize;
    }

    public void setPacketSize(int size){
        this.packetsize = size;
    }

    public void start(){
        ThreadManager.runDaemon(this, "PacketReceiver");
    }

    @Override
    public void run(){
        while(NetworkEngine.running() && !OpenGG.getEnded()){
            Packet packet = Packet.receive(socket, packetsize);
            byte[] bytes = packet.getData();
            byte type = packet.getData()[0];
            for(var processortuple : processors){
                if(processortuple.x == type || processortuple.x == 0)
                    processortuple.y.accept(packet);
            }
        }
    }

}
