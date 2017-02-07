/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import com.opengg.core.engine.GGConsole;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Javier
 */
public class ConnectionListener implements Runnable{
    Server server;
    ServerSocket ssocket;
    boolean close = false;
    
    public ConnectionListener(ServerSocket r, Server server){
        this.ssocket = r;
        this.server = server;
    }
    
    @Override
    public void run() {
        while(!close){
            try {
                Socket s = ssocket.accept();
                String ip = s.getInetAddress().getHostAddress();
                Date d = Calendar.getInstance().getTime();
                ServerClient sc = new ServerClient();
                sc.s = s;
                sc.ip = ip;
                sc.timeConnected = d;
                
                GGConsole.log("User connecting from " + ip);
                
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
                
                String handshake = in.readLine();
                System.out.println(handshake);
                
                if(!handshake.endsWith("hey server")){
                    GGConsole.log("Connection with " + ip + " failed");
                }
                
                out.println("hey client");
                handshake = in.readLine();
                
                if(!handshake.equals("oh shit we out here")){
                    GGConsole.log("Connection with " + ip + " failed");
                }
                
                out.println(server.name);
                GGConsole.log(ip + " connected to server, sending game state");
                server.addServerClient(sc);
            } catch (IOException ex) {
                GGConsole.warning("Client failed to connect!");
            }
            
        }
    }
    
    public void endServer(){
        close = true;
    }
}
