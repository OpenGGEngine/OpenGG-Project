 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network;

import com.opengg.core.console.GGConsole;
import com.opengg.core.network.client.Client;
import com.opengg.core.network.server.Server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

 /**
 *
 * @author Javier
 */
public class NetworkEngine {
    private static Server server;
    private static Client client;

    private static ConnectionManager receiver;

    public static void update(){
        if(server != null) server.update();
        if(client != null) client.update();
    }

    public static boolean isRunning(){
        if(server != null) return server.isRunning();
        if(client != null) return client.isRunning();
        return false;
    }

     public static Server getServer() {
         return server;
     }

     public static Client getClient() {
         return client;
     }

     public static Server initializeServer(String name, int port){
        try {
            var tcpsocket = new ServerSocket(port);
            var udpsocket = new DatagramSocket(port);

            var server = new Server(name, port, tcpsocket, udpsocket);

            GGConsole.log("Server initialized on port " + port);

            server.start();
            NetworkEngine.server = server;

            createReceiver(udpsocket, server.getPacketSize());
            receiver.addProcessor(PacketType.CLIENT_ACTION_UPDATE, server::accept);

            return server;
        } catch (IOException ex) {
            GGConsole.warning("Failed to create server");
            return null;
        }
    }

    public static Client connect(String ip, int port) {
        try {
            GGConsole.log("Connecting to " + ip + "...");
            var tcp = new Socket(ip, port);
            var udpsocket = new DatagramSocket();

            client = new Client(tcp, udpsocket, ConnectionData.get(tcp.getInetAddress(), port));

            client.start();

            client.doHandshake();
            GGConsole.log("Connected to " + ip + ", receiving world...");

            client.getData();
            GGConsole.log("Downloaded world, joining...");

            Thread.sleep(1000);

            createReceiver(udpsocket, client.getPacketSize());

            receiver.addProcessor(PacketType.SERVER_UPDATE, client::accept);
            receiver.addProcessor(PacketType.SERVER_COMPONENT_CREATE, client::acceptNewComponents);

            client.udpHandshake();

            return client;
        } catch (IOException ex) {
            GGConsole.warning("Failed to connect to server!");
            GGConsole.warn(ex.getLocalizedMessage());
            return null;
        }catch(InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void createReceiver(DatagramSocket socket, int packetsize){
        receiver = new ConnectionManager(socket, packetsize);
        receiver.start();
    }

    private NetworkEngine() {
    }
}
