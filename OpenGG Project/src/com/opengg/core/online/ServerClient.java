/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import java.net.Socket;
import java.util.Date;

/**
 *
 * @author Javier
 */
public class ServerClient {
    String ip;
    String name;
    Socket s;
    Date timeConnected;
    int latency;
}
