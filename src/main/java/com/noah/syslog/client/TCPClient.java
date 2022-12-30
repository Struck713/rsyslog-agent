package com.noah.syslog.client;

import com.noah.syslog.message.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient implements Client {

    private Socket socket;
    private PrintWriter writer;
    private InetAddress address;
    private int port;

    public TCPClient(InetAddress address, int port) {
        this.address = address;
        this.port = port;

        try {
            this.socket = new Socket(address, port);
            this.writer = new PrintWriter(this.socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Failed to open socket: " + address + ":" + port);
        }
    }

    @Override
    public void send(Message message) {
        this.writer.write(message.toString());
    }

    @Override
    public void close() {
        if (socket.isClosed()) return;
        try {
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Failed to close socket: " + this.address + ":" + this.port);
        }
    }

    @Override
    public String getName() {
        return "TCP";
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
