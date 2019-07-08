package com.opengg.core.network.common;

import java.net.InetAddress;
import java.util.Objects;

public class ConnectionData {
    public InetAddress address;
    public int port;

    public ConnectionData(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionData that = (ConnectionData) o;

        if (port != that.port) return false;
        if (!Objects.equals(address, that.address)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return address.toString() + "/" + port;
    }

    public static ConnectionData get(InetAddress address, int port){
        return new ConnectionData(address,port);
    }
}
