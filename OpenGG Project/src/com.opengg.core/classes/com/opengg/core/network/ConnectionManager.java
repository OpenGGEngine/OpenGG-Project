package com.opengg.core.network;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.math.Tuple;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.common.Packet;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.util.GGFuture;
import com.opengg.core.util.GGInputStream;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionManager implements Runnable{
    private final Map<Byte, List<Consumer<Packet>>> processors;
    private final List<Tuple<Tuple<Packet, GGFuture<Boolean>>, Float>> packetsWaiting = new ArrayList<>();
    private final List<Tuple<Tuple<Tuple<Long, Byte>, ConnectionData>, Float>> receivedPacketsWithAck = new ArrayList<>();
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
            packetsWaiting.add(Tuple.of(Tuple.of(packet,future), 0f));
            packet.send();
        }
    }

    public void receiveAcknowledgement(Packet packet){
        try {
            synchronized (SENT_BLOCK) {
                var packetTimestamp = new GGInputStream(packet.getData()).readLong();
                var foundPacket = packetsWaiting.stream()
                        .filter(p -> p.x.x.getTimestamp() == packetTimestamp)
                        .filter(p -> p.x.x.getConnection().equals(packet.getConnection()))
                        .peek(p -> p.x.y.set(true))
                        .collect(Collectors.toList());
                packetsWaiting.removeAll(foundPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validatePacket(Packet packet){
        if(packet.requestsAcknowledgement()){
            Packet.sendAcknowledgement(packet, packet.getConnection());

            synchronized (RECEIVED_BLOCK) {
                if (receivedPacketsWithAck.stream()
                        .noneMatch(c -> c.x.equals(Tuple.of(Tuple.of(packet.getTimestamp(), packet.getType()), packet.getConnection())))) {
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
                    .forEach(p -> p.x.x.send());
        }

        synchronized (RECEIVED_BLOCK){
            receivedPacketsWithAck.forEach(c -> c.y += delta);
            receivedPacketsWithAck.removeIf(c -> c.y >= 10f);
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
}
