 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.server;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.Serializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;

 /**
 *
 * @author Javier
 */
public class NewConnectionListener implements Runnable{
    private Server server;
    private boolean close = false;
    
    public NewConnectionListener(Server server){
        this.server = server;
    }
    
    @Override
    public void run() {
        while(!close && !OpenGG.getEnded()){
            try (Socket s = server.getTCPSocket().accept()) {
                var ip = s.getInetAddress().getHostAddress();
                var time = Instant.now();

                GGConsole.log("User connecting from " + ip);

                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);

                String handshake = in.readLine();

                if(!handshake.equals("hey server")){
                    GGConsole.log("Connection with " + ip + " failed");
                }
                out.println("hey client");

                handshake = in.readLine();
                if(!handshake.equals("oh shit we out here")){
                    GGConsole.log("Connection with " + ip + " failed");
                }

                out.println(server.getName());
                var name = in.readLine();

                var serverClient = new ServerClient(name, s.getInetAddress(), time);

                out.println(server.getPacketSize());
                out.println(serverClient.getId());

                in.readLine();
                out.println("to do is not there");
                out.println(Instant.now().toEpochMilli());

                GGConsole.log(ip + " connected to server, sending game state");

                byte[] bytes = Serializer.serialize(WorldEngine.getCurrent());
                out.println(bytes.length);
                in.read();

                s.getOutputStream().write(bytes);

                GGConsole.log(ip + " connected to server.");

                server.addServerClient(serverClient);
                
            } catch (IOException ex) {
                GGConsole.warning("Client failed to connect!");
            }           
        }
    }
    
    public void endServer(){
        close = true;
    }
}
