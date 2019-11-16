 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */

 package com.opengg.core.network;

 import com.opengg.core.console.GGConsole;
 import com.opengg.core.network.client.Client;
 import com.opengg.core.network.common.ConnectionData;
 import com.opengg.core.network.common.PacketType;
 import com.opengg.core.network.server.Server;

 import java.io.IOException;
 import java.net.DatagramSocket;
 import java.net.InetAddress;
 import java.net.UnknownHostException;

 /**
  * @author Javier
  */
 public class NetworkEngine {
     private static Server server;
     private static Client client;

     private static ConnectionManager connectionManager;
     private static BulkNetworkDataManager bulkNetworkDataManager;
     private static ChatManager chatManager;

     private static DatagramSocket socket;

     private NetworkEngine() {
     }

     /**
      * Updates all internal network managers by the given time
      * @param delta
      */
     public static void update(float delta) {
         if (server != null) server.update();
         if (client != null) client.update(delta);
         if (connectionManager != null) connectionManager.update(delta);
         if (bulkNetworkDataManager != null) bulkNetworkDataManager.update(delta);
     }

     /**
      * Returns if there is either a server or a client running
      * @return
      */
     public static boolean isRunning() {
         if (server != null) return true;
         if (client != null) return true;
         return true;
     }

     public static Server getServer() {
         return server;
     }

     public static Client getClient() {
         return client;
     }

     public static DatagramSocket getSocket() {
         return socket;
     }

     /**
      * Starts a server on the given port and using the given name
      * @param name
      * @param port
      * @return the Server instance resulting from starting a server, or null if the server failed to start
      */
     public static Server initializeServer(String name, int port) {
         try {
             socket = new DatagramSocket(port);
             server = new Server(name, port);

             createManagerInstances();

             GGConsole.log("Server initialized on port " + port);

             server.start();

             return server;
         } catch (IOException ex) {
             GGConsole.warning("Failed to create server");
             return null;
         }
     }

     /**
      * Connects to a server with the given IP address and port
      * <p>
      * Note, the Client instance returned is not guaranteed to have completed the process of connecting to the server.
      * To check if it has fully connected, use {@link Client#isRunning()}.
      * @param ip
      * @param port
      * @return
      */
     public static Client connect(ClientOptions options) {
         try {
             GGConsole.log("Connecting to " + options.data + "...");
             socket = new DatagramSocket();
             client = new Client(options);

             createManagerInstances();

             client.connect();

             return client;
         } catch (IOException ex) {
             GGConsole.warning("Failed to connect to server!");
             GGConsole.warn(ex.getLocalizedMessage());
             return null;
         }
     }

     public static ConnectionManager getPacketReceiver() {
         return connectionManager;
     }

     public static BulkNetworkDataManager getBulkNetworkDataManager() {
         return bulkNetworkDataManager;
     }

     public static ChatManager getChatManager() {
         return chatManager;
     }

     private static void createManagerInstances() {
         connectionManager = new ConnectionManager();
         connectionManager.start();
         bulkNetworkDataManager = new BulkNetworkDataManager();
         chatManager = new ChatManager();
     }

     public static class ClientOptions{
         public String username = "User";
         public ConnectionData data;

         public ClientOptions(String username, String ip, String port) {
             try {
                 this.username = username;
                 this.data = new ConnectionData(InetAddress.getByName(ip), Integer.parseInt(port));
             } catch (UnknownHostException e) {
                 e.printStackTrace();
             }
         }
     }

     public static class ServerOptions{

     }
 }
