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
import com.opengg.core.network.common.*;
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
    private final NetworkEngine.ServerOptions options;
    private List<ServerClient> clients;
    private boolean running;

    private List<ConnectionListener> listeners;
    private List<Component> newComponents;
    private List<Component> removedComponents;
    private List<Tuple<Component, Component>> movedComponents;

    private Map<Long, byte[]> lastUpdate = new HashMap<>();

    public Server(NetworkEngine.ServerOptions options){
        this.options = options;
        this.clients = new ArrayList<>();
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
        NetworkEngine.getPacketReceiver().addProcessor(PacketType.CONNECTION_END, p ->
                disconnect(this.getClient(p.getConnection()), this.getClient(p.getConnection()) + " has disconnected."));
        NetworkEngine.getPacketReceiver().addProcessor(PacketType.SERVER_INFO, this::sendServerInfo);
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

    private void sendServerInfo(Packet packet) {
        GGConsole.debug(packet.getConnection() + " requested server information");
        var info = new ServerInfo(options.name, options.motd, clients.size(), options.maxUsers);
        info.send(packet.getConnection());
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
            }
        }
        removal.forEach(c -> disconnect(c, c + " has timed out."));

        sendNewComponents();
        sendMovedComponents();
        sendDeletedComponents();
        sendState();
    }

    private void disconnect(ServerClient client, String reason) {
        GGConsole.log(reason);
        for(var listener : listeners)
            listener.onDisconnection(client);
        clients.remove(client);
    }

    private void sendState(){
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

    private void sendNewComponents(){
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

    private void saveNewComponent(Component component){
        if(component == null) return;
        if(component.shouldSerialize() == false) return;
        newComponents.add(component);
    }

    private void saveRemovedComponent(Component component){
        if(component == null) return;
        if(component.shouldSerialize() == false) return;
        removedComponents.add(component);
    }

    private void saveMovedComponent(Component component, Component parent){
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

    public NetworkEngine.ServerOptions getOptions() {
        return options;
    }

    public boolean isRunning() {
        return running;
    }

    private void accept(Packet packet){
        var packetSource = getClient(packet.getConnection());
        if(packetSource == null){
            GGConsole.warning("Received client update from unknown client: " + packet.getConnection());
            return;
        }
        packetSource.setLastMessage(Instant.now());
        packetSource.processData(new GGInputStream(packet.getData()));
    }

    private void onNewConnection(ReceivingBulkMessage message){
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

            String failReason = "";
            boolean validConnection = true;
            validConnection &= clients.size() < options.maxUsers;

            if(!validConnection)
                failReason = "Server is full";

            validConnection &= version.equals(GGInfo.getVersion());

            if(!validConnection)
                failReason = "Invalid version (Server is " + GGInfo.getVersion() + " while client is " + version + ")";

            validConnection &= application.equals(GGInfo.getApplicationName());

            if(!validConnection)
                failReason = "Invalid application";

            var out = new GGOutputStream();
            out.write(validConnection);
            if(validConnection){
                GGConsole.log("User at " + connectionData + " (" + username + ") validated, sending game state");
                var worldData = Serializer.serializeWorld(WorldEngine.getCurrent());
                out.write(options.name);
                out.write(ServerClient.getNextID());
                out.write(worldData.length);
                out.write(worldData);
            }else{
                out.write(failReason);
                GGConsole.warning(username + " failed to connect:" +
                        " Invalid application or version (using " + application + " v " + version +
                        ", server is " + GGInfo.getApplicationName() + " v " + GGInfo.getVersion() + ")");
            }

            boolean finalValidConnection = validConnection;
            NetworkEngine.getBulkNetworkDataManager().send(out.asByteArray(), "initialResponse", connectionData).thenRun(() -> {
                if(finalValidConnection){
                    addServerClient(new ServerClient(username, connectionData, connectionTime));
                    GGConsole.log(username + " connected");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
