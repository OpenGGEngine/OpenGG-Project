/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.online.Client;
import com.opengg.core.online.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Javier
 */
public class NetworkEngine {
    static String ip;
    
    public static Server initializeServer(String name, int port){
        ServerSocket s;
        try {
            s = new ServerSocket(port);
        } catch (IOException ex) {
            GGConsole.warning("Failed to create server");
            return null;
        }
        GGConsole.log("Server initialized on ports " + port + " and " + (port + 1));
        Server server = new Server(name, port, s);
        return server;
    }
    
    public static Client connect(String ip, int port){
        try {
            Socket s  = new Socket(ip, port);
            GGConsole.log("Connecting to " + ip);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
            
            Client c = new Client(s, ip, port);
            
            out.println("hey server");
            
            String handshake = in.readLine();
            
            if(!handshake.equals("hey client")){
                GGConsole.warning("Failed to connect to " + ip);
            }
            
            out.println("oh shit we out here");
            
            c.servName = in.readLine();
                     
            GGConsole.log("Connected to " + c.servName);
            
            return c;
        } catch (IOException ex) {
            GGConsole.warning("Failed to connect to server!");
            return null;
        }
    }
}
