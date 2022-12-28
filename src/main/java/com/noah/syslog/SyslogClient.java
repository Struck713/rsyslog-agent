package com.noah.syslog;

import com.noah.syslog.message.Message;

import java.io.IOException;
import java.net.*;

public class SyslogClient {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public SyslogClient(InetAddress address, int port) throws SocketException {
        this.address = address;
        this.port = port;
        this.socket = new DatagramSocket();
    }

    public void send(Message message) throws IOException {
        String rawMessage = message.toString();
        byte[] buf = rawMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, this.address, this.port);
        this.socket.send(packet);
    }

    public void close() {
        if (this.socket == null || this.socket.isClosed()) return;
        this.socket.close();
    }

}
