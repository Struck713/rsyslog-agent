package com.noah.syslog.message.enums;

import com.sun.jna.platform.win32.Advapi32Util;

public enum Severity {

    EMERGENCY(0, "Emergency"),
    ALERT(1, "Alert"),
    CRITICAL(2, "Critical"),
    ERROR(3, "Error"),
    WARNING(4, "Warning"),
    NOTICE(5, "Notice"),
    INFORMATIONAL(6, "Informational"),
    DEBUG(7, "Debug");

    public static Severity of(Advapi32Util.EventLogType type) {
        switch (type) {
            case AuditFailure:
            case Error: return Severity.ERROR;
            case Warning: return Severity.WARNING;
            case AuditSuccess:
            case Informational:
            default: return Severity.INFORMATIONAL;
        }
    }

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
