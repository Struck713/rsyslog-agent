package com.noah.syslog.message.enums;

public enum Severity {

    EMERGENCY(0, "Emergency"),
    ALERT(1, "Alert"),
    CRITICAL(2, "Critical"),
    ERROR(3, "Error"),
    WARNING(4, "Warning"),
    NOTICE(5, "Notice"),
    INFORMATIONAL(6, "Informational"),
    DEBUG(7, "Debug");

    private int id;
    private String name;

    Severity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
