package com.noah.syslog.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConfigHost {

    private String protocol = "UDP";
    private String address;
    private int port;

    public String getProtocol() { return this.protocol; }

    public String getAddress() { return this.address; }
    public int getPort() { return this.port; }

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByName(this.address);
    }


}
