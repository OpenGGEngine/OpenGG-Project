package com.opengg.core.network.server;

public interface ConnectionListener {
    void onConnection(ServerClient user);
    void onDisconnection(ServerClient user);
}
