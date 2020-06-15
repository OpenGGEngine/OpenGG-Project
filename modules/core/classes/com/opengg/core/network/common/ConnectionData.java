package com.opengg.core.network.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class ConnectionData {
    public InetAddress address;
    public int port;

    public ConnectionData(String address, String port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

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
        return Objects.equals(address, that.address);
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
