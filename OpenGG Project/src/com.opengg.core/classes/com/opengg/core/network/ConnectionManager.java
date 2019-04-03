package com.opengg.core.network;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.thread.ThreadManager;

import java.net.DatagramSocket;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionManager implements Runnable{
    private final Map<Byte, List<Consumer<Packet>>> processors; //since im definitely going to forget why the tuple contains a byte its a packet identifier
    private final List<Packet> packetsWaiting = new ArrayList<>();
    private final DatagramSocket socket;
    private int packetsize;

    public ConnectionManager(DatagramSocket socket, int packetsize){
        processors = new HashMap<>();
        this.socket = socket;
        this.packetsize = packetsize;
    }

    public void addProcessor(byte type, Consumer<Packet> processor){
        processors.merge(type,
                List.of(processor),
                (l1,l2) -> Stream.concat(l1.stream(),l2.stream()).collect(Collectors.toList()));
    }

    public int getPacketSize(){
        return packetsize;
    }

    public void setPacketSize(int size){
        this.packetsize = size;
    }

    public void start(){
        ThreadManager.runDaemon(this, "ConnectionManager");
    }

    public void sendWithAcknowledgement(Packet packet){

    }

    public void receiveAcknowledgement(Packet packet){

    }

    @Override
    public void run(){
        while(NetworkEngine.isRunning() && OpenGG.getEnded()){
            Packet packet = Packet.receive(socket);
            processors.get(packet.getType()).forEach(p -> p.accept(packet));
        }
    }

}
