/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.server;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.online.Packet;
import com.opengg.core.world.Serializer;
import java.io.BufferedReader;
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
    int curid;
    
    public ConnectionListener(ServerSocket r, Server server){
        this.ssocket = r;
        this.server = server;
    }
    
    @Override
    public void run() {
        while(!close && !OpenGG.getEnded()){
            try {
                Socket s = ssocket.accept();
                String ip = s.getInetAddress().getHostAddress();
                Date d = Calendar.getInstance().getTime();
                ServerClient sc = new ServerClient();
                sc.ip = s.getInetAddress();
                sc.timeConnected = d;
                sc.id = curid;
                curid++;
                
                GGConsole.log("User connecting from " + ip);
                
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
                
                String handshake = in.readLine();
                
                if(!handshake.equals("hey server")){GGConsole.log("Connection with " + ip + " failed");}
                
                out.println("hey client");
                handshake = in.readLine();
                
                if(!handshake.equals("oh shit we out here")){GGConsole.log("Connection with " + ip + " failed");}
                               
                out.println(server.name);
                sc.name = in.readLine();
                
                GGConsole.log(ip + " connected to server, sending game state");
                
                byte[] bytes = Serializer.serialize(OpenGG.curworld);
                
                out.println(bytes.length);
                
                s.getOutputStream().write(bytes);
                
                out.println(server.packetsize);
                s.close();

                Packet p = Packet.receive(server.dsocket, server.packetsize);
                Packet.send(server.dsocket, p.getData(), sc.ip, p.getPort());
                sc.port = p.getPort();
                
                GGConsole.log(ip + " connected to server.");
                
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
