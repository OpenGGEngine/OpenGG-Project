package com.opengg.core.network.common;

import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class ServerInfo {
    public String name;
    public String motd;
    public int users;
    public int maxUsers;

    public float ping;

    public ServerInfo(String name, String motd, int users, int maxUsers) {
        this.name = name;
        this.motd = motd;
        this.users = users;
        this.maxUsers = maxUsers;
    }

    public ServerInfo(GGInputStream in){
        try {
            name = in.readString();
            motd = in.readString();
            users = in.readInt();
            maxUsers = in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(ConnectionData target){
        try {
            var out = new GGOutputStream();
            out.write(name);
            out.write(motd);
            out.write(users);
            out.write(maxUsers);
            Packet.send(PacketType.SERVER_INFO, out.asByteArray(), target);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "name='" + name + '\'' +
                ", motd='" + motd + '\'' +
                ", users=" + users +
                ", maxUsers=" + maxUsers +
                '}';
    }
}
