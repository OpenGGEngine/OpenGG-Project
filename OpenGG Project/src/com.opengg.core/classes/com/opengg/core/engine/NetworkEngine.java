 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.online.client.Client;
import com.opengg.core.online.Packet;
import com.opengg.core.online.server.Server;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.World;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author Javier
 */
public class NetworkEngine { 
    public static Server initializeServer(String name, int port){
        ServerSocket s;
        DatagramSocket ds;
        try {
            s = new ServerSocket(port);
            ds = new DatagramSocket(port);
        } catch (IOException ex) {
            GGConsole.warning("Failed to create server");
            return null;
        }
        GGConsole.log("Server initialized on ports " + port + " and " + (port + 1));
        Server server = new Server(name, port, s, ds);
        return server;
    }
    
    public static Client connect(String ip, int port){
        try {
            Socket s  = new Socket(ip, port);
            DatagramSocket ds = new DatagramSocket();
            GGConsole.log("Connecting to " + s.getInetAddress().getHostAddress());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);

            out.println("hey server");
            
            String handshake = in.readLine();
            if(!handshake.equals("hey client")){GGConsole.warning("Failed to connect to " + s.getInetAddress().getHostAddress());}
            
            out.println("oh shit we out here");
            
            String servname = in.readLine();
            out.println(OpenGG.getApp().applicationName);
                     
            GGConsole.log("Connected to " + servname + ", receiving world...");
            
            int worldsize = Integer.decode(in.readLine());
            byte[] bytes = new byte[worldsize];
            s.getInputStream().read(bytes);     
            World w = Deserializer.deserialize(ByteBuffer.wrap(bytes));
            WorldEngine.useWorld(w);
            
            int packetsize = Integer.decode(in.readLine());
            InetAddress address = s.getInetAddress();
            s.close();
            
            Packet.send(ds, new byte[packetsize], address, port);
            
            GGConsole.log("Connected to " + servname);
            
            Client c = new Client(ds, address, port, servname, packetsize);
            
            return c;
        } catch (IOException ex) {
            GGConsole.warning("Failed to connect to server!");
            return null;
        }
    }
}
