/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.client;

import com.opengg.core.Configuration;
import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Executor;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.common.Packet;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.network.common.ReceivingBulkMessage;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.util.LambdaContainer;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Javier
 */
public class Client {
    private String servName;
    private String username = "Testo";

    private final ConnectionData connectionData;
    private final Instant timeConnected;
    private int packetsize;
    private boolean running;
    private float currentDelay;
    private final float delayRecountTime = 0.25f;

    private final List<Runnable> onDisconnectListeners = new ArrayList<>();

    private ActionQueuer queuer;
    
    public Client(NetworkEngine.ClientOptions options){
        this.connectionData = options.data;
        this.username = options.username;
        this.timeConnected = Instant.now();
    }

    public void connect(){
        var out = new GGOutputStream();
        try {
            out.write("ratio tile");
            out.write(GGInfo.getApplicationName());
            out.write(GGInfo.getVersion());
            out.write(this.username);

            NetworkEngine.getPacketReceiver().addProcessor(PacketType.SERVER_UPDATE, this::acceptUpdate);
            NetworkEngine.getPacketReceiver().addProcessor(PacketType.SERVER_COMPONENT_CREATE, this::acceptNewComponents);
            NetworkEngine.getPacketReceiver().addProcessor(PacketType.SERVER_COMPONENT_REMOVE, this::acceptRemovedComponent);
            NetworkEngine.getPacketReceiver().addProcessor(PacketType.SERVER_COMPONENT_MOVE, this::acceptMovedComponent);
            var container = new LambdaContainer<Boolean>();
            container.value = false;
            NetworkEngine.getBulkNetworkDataManager().onMessageArrival(m -> {
                if(m.getName().equals("initialResponse")){
                    processInitialResponse(m);
                    container.value = true;
                }
            });

            GGConsole.log("Connecting to " + connectionData);
            NetworkEngine.getBulkNetworkDataManager().send(out.asByteArray(), "initial", connectionData);
            GGConsole.log("Sent initial connection request to " + connectionData);

            var sendTime = System.currentTimeMillis();
            while(!container.value){
                if(System.currentTimeMillis() > sendTime + 5000)
                    throw new RuntimeException("Server timed out on connection");
                Thread.sleep(10);
                NetworkEngine.update(0.01f);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processInitialResponse(ReceivingBulkMessage message){
        var in = new GGInputStream(message.getAllData());
        try {
            if(!in.readBoolean()){
                throw new RuntimeException(in.readString());
            }

            servName = in.readString();
            var userId = in.readInt();
            GGInfo.setUserId(userId);

            var worldLength = in.readInt();
            GGConsole.log("Validated game version, receiving world (" + worldLength+ " bytes)");
            var worldBuffer = in.readByteArray(worldLength);

            OpenGG.asyncExec(() -> {
                var world = Deserializer.deserializeWorld(ByteBuffer.wrap(worldBuffer));
                WorldEngine.setOnlyActiveWorld(world);

                this.queuer = ActionQueuer.get();
                GGConsole.log("Successfully connected to " + servName + "!");

                running = true;
            });

            Executor.every(Duration.ofMillis((long) (delayRecountTime*1000)),
                    () -> {
                        if(this.running)
                            NetworkEngine.getClient().calculateLatency().thenAccept(c -> this.currentDelay = c.floatValue());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(float delta){
        try {
            if(!running) return;
            var out = new GGOutputStream();

            out.write(MouseController.get().multiply(Configuration.getFloat("sensitivity")));
            queuer.writeData(out);

            var data = ((ByteArrayOutputStream)out.getStream()).toByteArray();
            Packet.sendGuaranteed(PacketType.CLIENT_ACTION_UPDATE, data, connectionData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptUpdate(Packet packet){
        byte[] bytes = packet.getData();
        var in = new GGInputStream(bytes);

        OpenGG.asyncExec(() -> {
            try {
                short amount = in.readShort();
                var delta = this.currentDelay/(1000*2);
                for (int i = 0; i < amount; i++) {
                    long id = in.readLong();
                    WorldEngine.findEverywhereByGUID(id)
                            .ifPresentOrElse(c -> {
                                try {
                                    c.deserializeUpdate(in, 0);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }, () -> {
                                GGConsole.warning("Failed to find component of id " + id + " during server deserialization");
                                //WorldEngine.getCurrent().getWorld().printLayout();
                            });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void acceptNewComponents(Packet packet) {
            OpenGG.asyncExec(() -> {
                try {
                    var in = new GGInputStream(packet.getData());
                    var compAmount = in.readInt();
                    var loadedCompList = new ArrayList<Deserializer.SerialHolder>();

                    for (int i = 0; i < compAmount; i++) {
                        loadedCompList.add(Deserializer.deserializeSingleComponent(in));
                    }

                    for (var comp : loadedCompList) {
                        WorldEngine.findEverywhereByGUID(comp.parent)
                                .ifPresentOrElse(c -> c.attach(comp.comp),
                                        () ->  loadedCompList.stream()
                                                .filter(c -> c.comp.getGUID() == comp.parent)
                                                .findFirst()
                                                .ifPresentOrElse(c -> c.comp.attach(comp.comp),
                                                        () -> GGConsole.warning("Failed to find component " + comp.parent + " while deserializing " + comp.comp.getName())));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

    }

    private void acceptRemovedComponent(Packet packet) {
        try {
            var in = new GGInputStream(packet.getData());
            var amount = in.readInt();
            for(int i = 0; i < amount; i++){
                var comp = in.readLong();
                WorldEngine.findEverywhereByGUID(comp).ifPresent(Component::delete);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void acceptMovedComponent(Packet packet) {
        try {
            var in = new GGInputStream(packet.getData());
            var amount = in.readInt();
            for(int i = 0; i < amount; i++){
                var compID = in.readLong();
                var newC = in.readLong();

                WorldEngine.findEverywhereByGUID(compID).ifPresent(cc -> {
                    WorldEngine.findEverywhereByGUID(newC).ifPresent(nc -> nc.attach(cc));
                });
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void disconnect(){
        Packet.send(PacketType.CONNECTION_END, new byte[]{}, getConnection());
        GGConsole.log("Disconnected from server");
        this.running = false;
        WorldEngine.setOnlyActiveWorld(new World());
        onDisconnectListeners.forEach(Runnable::run);
        NetworkEngine.endManagers();

    }

    public void addDisconnectListener(Runnable disconnect){
        onDisconnectListeners.add(disconnect);
    }

    public ConnectionData getConnection() {
        return connectionData;
    }

    public String getServerName() {
        return servName;
    }

    public Instant getTimeConnected() {
        return timeConnected;
    }

    public CompletableFuture<Long> calculateLatency()  {
        var pre = System.currentTimeMillis();
        var future = new CompletableFuture<Long>();
        Packet.sendGuaranteed(PacketType.PING, new byte[]{}, NetworkEngine.getClient().getConnection())
                .whenComplete(b -> future.complete(System.currentTimeMillis()-pre));
        return future;
    }

    public boolean isRunning(){
        return running;
    }

    public int getPacketSize(){
        return packetsize;
    }

    public String getUsername() {
        return username;
    }
}
