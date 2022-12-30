package com.noah.syslog.client;

import com.noah.syslog.message.Message;

import java.net.InetAddress;

public interface Client {

    String getName();
    InetAddress getAddress();
    int getPort();

    void send(Message message);
    void close();

}
