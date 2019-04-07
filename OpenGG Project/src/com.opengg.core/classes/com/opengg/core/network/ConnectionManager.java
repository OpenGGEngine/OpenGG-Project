package com.opengg.core.network;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.math.Tuple;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.util.GGInputStream;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionManager implements Runnable{
    private final Map<Byte, List<Consumer<Packet>>> processors; //since im definitely going to forget why the tuple contains a byte its a packet identifier
    private final List<Tuple<Packet, Float>> packetsWaiting = new ArrayList<>();
    private final List<Tuple<Tuple<Tuple<Long, Byte>, ConnectionData>, Float>> receivedPacketsWithAck = new ArrayList<>();
    private final DatagramSocket socket;
    private int packetsize;
    private final float RESEND_DELAY = 0.1f;

    private final static Object RECEIVED_BLOCK = new Object();
    private final static Object SENT_BLOCK = new Object();


    public ConnectionManager(DatagramSocket socket, int packetsize){
        processors = new HashMap<>();
        this.socket = socket;
        this.packetsize = packetsize;

        this.addProcessor(PacketType.ACK, this::receiveAcknowledgement);
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
        synchronized (SENT_BLOCK) {
            packetsWaiting.add(Tuple.of(packet, 0f));
            packet.send();
        }
    }

    public void receiveAcknowledgement(Packet packet){
        try {
            synchronized (SENT_BLOCK) {
                var packetTimestamp = new GGInputStream(packet.getData()).readLong();
                var foundPacket = packetsWaiting.stream()
                        .filter(p -> p.x.getTimestamp() == packetTimestamp)
                        .filter(p -> p.x.getConnection().equals(packet.getConnection()))
                        .collect(Collectors.toList());
                packetsWaiting.removeAll(foundPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validatePacket(Packet packet){
        if(packet.requestsAcknowledgement()){
            Packet.sendAcknowledgement(NetworkEngine.getSocket(), packet, packet.getConnection());
            synchronized (RECEIVED_BLOCK) {
                if (!receivedPacketsWithAck.stream()
                        .anyMatch(c -> c.x.equals(Tuple.of(Tuple.of(packet.getTimestamp(), packet.getType()), packet.getConnection())))) {

                    receivedPacketsWithAck.add(Tuple.of(Tuple.of(Tuple.of(packet.getTimestamp(), packet.getType()), packet.getConnection()), 0f));

                    return true;
                } else {
                    return false;
                }
            }
        }else{
            return true;
        }
    }

    public void update(float delta){
        synchronized (SENT_BLOCK) {
            packetsWaiting.stream()
                    .peek(p -> p.y += delta)
                    .filter(p -> p.y >= RESEND_DELAY)
                    .peek(p -> p.y = 0f)
                    .peek(p -> PerformanceManager.registerPacketResent())
                    .forEach(p -> p.x.send());
        }

        synchronized (RECEIVED_BLOCK){
            receivedPacketsWithAck.forEach(c -> c.y += delta);
            receivedPacketsWithAck.removeIf(c -> c.y >= 1f);
        }
    }

    @Override
    public void run(){
        while(NetworkEngine.isRunning() && OpenGG.getEnded()){
            Packet packet = Packet.receive(socket);
            if(validatePacket(packet))
                processors.getOrDefault(packet.getType(), List.of()).forEach(p -> p.accept(packet));
        }
    }


}
