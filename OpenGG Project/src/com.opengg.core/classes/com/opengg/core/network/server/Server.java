/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.server;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Tuple;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.common.Packet;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.network.common.ReceivingBulkMessage;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.ActionTransmitterComponent;
import com.opengg.core.world.components.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Javier
 */
public class Server {
    private String name;
    private List<ServerClient> clients;
    private List<ServerClient> newclients;

    private boolean running;
    private int port;

    private List<ConnectionListener> listeners;
    private List<Component> newComponents;
    private List<Component> removedComponents;
    private List<Tuple<Component, Component>> movedComponents;

    private Map<Long, byte[]> lastUpdate = new HashMap<>();

    public Server(String name, int port){
        this.name = name;
        this.port = port;
        this.clients = new ArrayList<>();
        this.newclients = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.newComponents = new ArrayList<>();
        this.removedComponents = new ArrayList<>();
        this.movedComponents = new ArrayList<>();
        WorldEngine.addComponentAdditionListener(this::saveNewComponent);
        WorldEngine.addComponentRemovalListener(this::saveRemovedComponent);
        WorldEngine.addComponentMoveListener(this::saveMovedComponent);
    }

    public void start(){
        running = true;

        NetworkEngine.getPacketReceiver().addProcessor(PacketType.CLIENT_ACTION_UPDATE, this::accept);
        NetworkEngine.getChatManager().addServerChatConsumer(m -> {
            GGConsole.log("(Chat) " + m.toString());
            if(m.getContents().charAt(0) != '/'){
                getClients().forEach(c -> {
                    m.send(c.getConnection());
                });
            }
        });
        NetworkEngine.getBulkNetworkDataManager().onMessageArrival(m -> {
            if(m.getName().equals("initial")){
                onNewConnection(m);
            }
        });
    }

    public void update(){
        var alltransmitters = WorldEngine.getCurrent().getAllDescendants().stream()
                .filter(c -> c instanceof ActionTransmitterComponent)
                .map(c -> (ActionTransmitterComponent)c)
                .collect(Collectors.toList());

        for(var client : clients){
            for(var transmitter : alltransmitters){
                if(transmitter.getUserId() == client.getId() && !client.getTransmitters().contains(transmitter)){
                    client.getTransmitters().add(transmitter);
                }
            }
        }

        var removal = new ArrayList<ServerClient>();

        for(var client : this.clients){
            if(client.getLastMessage() != null && Instant.now().toEpochMilli() - client.getLastMessage().toEpochMilli() > 5000){
                removal.add(client);
                GGConsole.log(client + " has timed out");
                for(var listener : listeners)
                    listener.onDisconnection(client);
                //todo
            }
        }

        clients.removeAll(removal);

        sendNewComponents();
        sendMovedComponents();
        sendDeletedComponents();
        sendState();
    }

    public void sendState(){
        try {
            var componentsToSerialize = Stream.concat(WorldEngine.getCurrent().getAllDescendants().stream(), List.of(WorldEngine.getCurrent()).stream())
                    .filter(Component::shouldSerializeUpdate)
                    .filter(Component::shouldSerialize)
                    .collect(Collectors.toList());

            List<byte[]> processedComponents = new ArrayList<>();
            for(var comp : componentsToSerialize){
                GGOutputStream compOut = new GGOutputStream();
                compOut.write(comp.getGUID());
                comp.serializeUpdate(compOut);

                if(!Arrays.equals(lastUpdate.get(comp.getGUID()), compOut.asByteArray()) || new Random().nextInt(6) == 0){
                    lastUpdate.put(comp.getGUID(), compOut.asByteArray());
                    processedComponents.add(compOut.asByteArray());
                }
            }

            var componentsToSend = new ArrayList<>(processedComponents);

            GGOutputStream out = new GGOutputStream();

            out.write((short)componentsToSend.size());
            for(var compArray : componentsToSend) {
                out.write(compArray);
            }

            for(var client : clients){
                Packet.send(PacketType.SERVER_UPDATE, out.asByteArray(), client.getConnection());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNewComponents(){
        if(newComponents.isEmpty()) return;
        try {

            var out = new GGOutputStream();
            out.write(newComponents.size());
            for(var comp : newComponents){
                out.write(Serializer.serializeSingleComponent(comp));
            }

            newComponents.clear();

            var bytes = out.asByteArray();
            for(var client : clients){
                Packet.sendGuaranteed(PacketType.SERVER_COMPONENT_CREATE, bytes, client.getConnection());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDeletedComponents() {
        if(!removedComponents.isEmpty()){
            try {
                var out = new GGOutputStream();
                out.write(removedComponents.size());
                for(var comp : removedComponents){
                    out.write(comp.getGUID());
                }
                for(var client : clients){
                    Packet.sendGuaranteed(PacketType.SERVER_COMPONENT_REMOVE, out.asByteArray(), client.getConnection());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        removedComponents.clear();
    }

    private void sendMovedComponents() {
        if(!movedComponents.isEmpty()){
            try {

                var out = new GGOutputStream();
                out.write(movedComponents.size());
                for(var comp : movedComponents){
                    out.write(comp.x.getGUID());
                    out.write(comp.y.getGUID());

                }
                for(var client : clients){
                    Packet.sendGuaranteed(PacketType.SERVER_COMPONENT_MOVE, out.asByteArray(), client.getConnection());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        movedComponents.clear();
    }

    public void saveNewComponent(Component component){
        if(component == null) return;
        if(component.shouldSerialize() == false) return;
        newComponents.add(component);
    }

    public void saveRemovedComponent(Component component){
        if(component == null) return;
        if(component.shouldSerialize() == false) return;
        removedComponents.add(component);
    }

    public void saveMovedComponent(Component component, Component parent){
        if(component == null) return;
        if(component.shouldSerialize() == false) return;
        if(!parent.shouldSerialize()) throw new IllegalStateException("Serializable component attached to non-serializable one");
        movedComponents.add(Tuple.of(component, parent));
    }

    public List<ServerClient> getClients() {
        return clients;
    }

    public ServerClient getClient(ConnectionData data){
        var tempclients =  List.copyOf(clients);
        return tempclients.stream()
                .filter(client -> client.getConnection().equals(data))
                .findFirst().orElse(null);
    }

    public List<ServerClient> getClients(InetAddress ip){
        var tempclients =  List.copyOf(clients);
        return tempclients.stream()
                .filter(client -> client.getConnection().getAddress().equals(ip))
                .collect(Collectors.toList());
    }

    public Optional<ServerClient> getClientByID(int id){
        return clients.stream().filter(c -> c.getId() == id).findFirst();
    }

    public List<ServerClient> getClientsByName(String name){
        return clients.stream().filter(c -> c.getName().equals(name)).collect(Collectors.toList());
    }

    public void subscribe(ConnectionListener listener){
        listeners.add(listener);
    }

    public void addServerClient(ServerClient sc){
        clients.add(sc);

        for(var listener : listeners)
            listener.onConnection(sc);
    }

    public String getName() {
        return name;
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    public void accept(Packet packet){
        var packetSource = getClient(packet.getConnection());
        packetSource.setLastMessage(Instant.now());
        packetSource.processData(new GGInputStream(packet.getData()));
    }

    public void onNewConnection(ReceivingBulkMessage message){
        var in = new GGInputStream(message.getAllData());
        var connectionData = message.getSource();
        var connectionTime = Instant.now();

        GGConsole.log("Receiving new connection from " + connectionData);
        try {
            var check = in.readString();
            if(!check.equals("ratio tile"))
                throw new RuntimeException("Invalid header for new connection message!");
            var application = in.readString();
            var version = in.readString();
            var username = in.readString();

            boolean validConnection = application.equals(GGInfo.getApplicationName()) && version.equals(GGInfo.getVersion());

            var out = new GGOutputStream();
            out.write(validConnection);
            if(validConnection){
                GGConsole.log("User at " + connectionData + " (" + username + ") validated, sending game state");
                var worldData = Serializer.serializeWorld(WorldEngine.getCurrent());
                out.write(ServerClient.getNextID());
                out.write(worldData.length);
                out.write(worldData);
            }else{
                GGConsole.warning(username + " failed to connect:" +
                        " Invalid application or version (using " + application + " v " + version +
                        ", server is " + GGInfo.getApplicationName() + " v " + GGInfo.getVersion() + ")");
            }

            NetworkEngine.getBulkNetworkDataManager().send(out.asByteArray(), "initialResponse", connectionData).thenRun(() -> {
                if(validConnection){
                    addServerClient(new ServerClient(username, connectionData, connectionTime));
                    GGConsole.log(username + " connected");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
