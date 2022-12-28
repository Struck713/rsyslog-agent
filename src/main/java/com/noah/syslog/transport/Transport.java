package com.noah.syslog.transport;

public enum Transport {

    TCP(0),
    UDP(1);

    private int id;

    Transport(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
}
