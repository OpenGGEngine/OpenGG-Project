package com.opengg.core.network.client;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;

import java.io.*;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Base64;

public class ConnectionInitializationThread {
    public void doHandshake() throws IOException {
        /*var in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        var out = new PrintWriter(new OutputStreamWriter(tcpSocket.getOutputStream()), true);

        out.println("hey server");
        var handshake = in.readLine();

        if (!handshake.equals("hey client")) {
            GGConsole.warning("Failed to connect to " + connectionData.address + ", invalid handshake");
        }

        out.println("oh shit we out here");
        servName = in.readLine();
        out.println(OpenGG.getApp().applicationName);

        packetsize = Integer.decode(in.readLine());
        int id = Integer.decode(in.readLine());

        var setToRun = Instant.now();
        out.println("Sonic");

        in.readLine();
        var end = Instant.now();

        latency = end.toEpochMilli() - setToRun.toEpochMilli();

        var time = in.readLine();
        var longtime = Long.decode(time) + (latency/2);

        timedifference = end.toEpochMilli() - longtime;

        GGInfo.setUserId(id);

        byte[] decodedBytes = Base64.getDecoder().decode(in.readLine());
        GGConsole.log("Downloading world (" + decodedBytes.length + " bytes)");

        var buffer = ByteBuffer.wrap(decodedBytes);

        World w = Deserializer.deserializeWorld(buffer);
        WorldEngine.setOnlyActiveWorld(w);

        GGConsole.log("World downloaded");
        GGConsole.log("Connected to " + tcpSocket.getInetAddress());*/
    }
}
