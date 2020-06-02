package com.opengg.core.network;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.common.Packet;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.util.GGFuture;
import com.opengg.core.util.GGInputStream;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionManager implements Runnable{
    private final Map<Byte, List<Consumer<Packet>>> processors;
    private final Map<SentPacketTracker, Float> packetsWaiting = new HashMap<>();
    private final Map<ReceivedPacketTracker, Float> receivedPacketsWithAck = new HashMap<>();
    private final HashMap<Byte, List<GGFuture<Packet>>> waitingForPacket = new HashMap<>();
    private final float RESEND_DELAY = 0.2f;

    private final static Object RECEIVED_BLOCK = new Object();
    private final static Object SENT_BLOCK = new Object();

    public ConnectionManager(){
        processors = new HashMap<>();

        this.addProcessor(PacketType.ACK, this::receiveAcknowledgement);
    }

    public void addProcessor(byte type, Consumer<Packet> processor){
        processors.merge(type,
                List.of(processor),
                (l1,l2) -> Stream.concat(l1.stream(),l2.stream()).collect(Collectors.toList()));
    }

    public void start(){
        ThreadManager.runDaemon(this, "ConnectionManager");
    }

    public void sendWithAcknowledgement(Packet packet, GGFuture<Boolean> future){
        synchronized (SENT_BLOCK) {
            packetsWaiting.put(new SentPacketTracker(packet, future), 0f);
            packet.send();
        }
    }

    public void receiveAcknowledgement(Packet packet){
        try {
            synchronized (SENT_BLOCK) {
                var packetTimestamp = new GGInputStream(packet.getData()).readLong();
                var foundPacket = packetsWaiting.keySet().stream()
                        .filter(p -> p.packet().getTimestamp() == packetTimestamp)
                        .filter(p -> p.packet().getConnection().equals(packet.getConnection()))
                        .peek(p -> p.future().set(true))
                        .collect(Collectors.toList());
                foundPacket.forEach(packetsWaiting::remove);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validatePacket(Packet packet){
        if(packet.requestsAcknowledgement()){
            Packet.sendAcknowledgement(packet, packet.getConnection());

            synchronized (RECEIVED_BLOCK) {
                if (!receivedPacketsWithAck.containsKey(new ReceivedPacketTracker(packet.getTimestamp(), packet.getConnection(), packet.getType()))) {
                    receivedPacketsWithAck.put(new ReceivedPacketTracker(packet.getTimestamp(), packet.getConnection(), packet.getType()), 0f);
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
            for(var packet : packetsWaiting.keySet()){
                float time = packetsWaiting.get(packet);
                time += delta;
                if(time > RESEND_DELAY){
                    time = 0;
                    PerformanceManager.registerPacketResent();
                    packet.packet.send();
                }
                packetsWaiting.put(packet, time);
            }
        }

        synchronized (RECEIVED_BLOCK){
            for(var key : receivedPacketsWithAck.keySet()){
                if(receivedPacketsWithAck.get(key) >= 10f) receivedPacketsWithAck.remove(key);
                else receivedPacketsWithAck.put(key, receivedPacketsWithAck.get(key) - delta);
            }
        }

    }

    @Override
    public void run(){
        while(NetworkEngine.isRunning() && OpenGG.getEnded()){
            Packet packet = Packet.receive();
            if(validatePacket(packet)) {
                processors.getOrDefault(packet.getType(), List.of()).forEach(p -> p.accept(packet));
            }
        }
    }

    record ReceivedPacketTracker(long timestamp, ConnectionData source, byte type){}
    record SentPacketTracker(Packet packet, GGFuture<Boolean> future){}
}
