package com.opengg.core.network.common;

import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class ChatMessage {
    private ConnectionData source = null;
    private String user;
    private String contents;

    public ChatMessage(String user, String contents) {
        this.user = user;
        this.contents = contents;
    }

    public ChatMessage(Packet packet){
        try {
            this.source = packet.getConnection();
            var in = new GGInputStream(packet.getData());
            this.user = in.readString();
            this.contents = in.readString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(ConnectionData data){
        try {
            var out = new GGOutputStream();
            out.write(user);
            out.write(contents);

            Packet.sendGuaranteed(PacketType.CHAT, out.asByteArray(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUser() {
        return user;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return user + ": " + contents;
    }
}
