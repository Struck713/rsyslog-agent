package com.noah.syslog.client;

import com.noah.syslog.message.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClient implements Client {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public UDPClient(InetAddress address, int port) throws SocketException {
        this.address = address;
        this.port = port;
        this.socket = new DatagramSocket();
    }

    @Override
    public void send(Message message) {
        String rawMessage = message.toString();
        byte[] buf = rawMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, this.address, this.port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (this.socket == null || this.socket.isClosed()) return;
        this.socket.close();
    }

    @Override
    public String getName() {
        return "UDP";
    }

    @Override
    public InetAddress getAddress() {
        return this.address;
    }

    @Override
    public int getPort() {
        return this.port;
    }

}
