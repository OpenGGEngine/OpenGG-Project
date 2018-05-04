 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import com.opengg.core.console.GGConsole;
import com.opengg.core.online.client.Client;
import com.opengg.core.online.server.Server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

 /**
 *
 * @author Javier
 */
public class NetworkEngine {
    public static Server initializeServer(String name, int port){
        try {
            var tcpsocket = new ServerSocket(port);
            var udpsocket = new DatagramSocket(port);

            var server = new Server(name, port, tcpsocket, udpsocket);

            GGConsole.log("Server initialized on port " + port);

            server.start();

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
            var udp = new DatagramSocket();

            var client =  new Client(tcp, udp, tcp.getInetAddress(), port);

            client.start();

            client.doHandshake();
            GGConsole.log("Connected to " + ip + ", receiving world...");

            client.getData();
            GGConsole.log("Downloaded world, joining...");

            return client;
        } catch (IOException ex) {
            GGConsole.warning("Failed to connect to server!");
            return null;
        }
    }

    private NetworkEngine() {
    }
}
