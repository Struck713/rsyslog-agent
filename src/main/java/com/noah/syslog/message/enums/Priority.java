package com.noah.syslog.message.enums;

public enum Priority {

    KERNEL(0, "kernel"),
    USER(1, "user"),
    MAIL(2, "mail"),
    DAEMON(3, "daemon"),
    SECURITY_AUTH_1(4, "security/authorization"),
    SYSLOGD(5, "syslogd"),
    LINE_PRINTER(6, "line printer"),
    NETWORK_NEWS(7, "network news"),
    UUCP(8, "UUCP"),
    CLOCK_1(9, "clock"),
    SECURITY_AUTH_2(10, "security/authorization"),
    FTP(11, "FTP"),
    NTP(12, "NTP"),
    LOG_AUDIT(13, "log audit"),
    LOG_ALERT(14, "log alert"),
    CLOCK_2(15, "clock"),
    LOCAL_0(16, "local use 0"),
    LOCAL_1(17, "local use 1"),
    LOCAL_2(18, "local use 2"),
    LOCAL_7(23, "local use 3"),
    LOCAL_3(19, "local use 4"),
    LOCAL_4(20, "local use 5"),
    LOCAL_5(21, "local use 6"),
    LOCAL_6(22, "local use 7");

    private int id;
    private String name;

    Priority(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int with(Severity severity) {
        return (this.getID() * 8) + severity.getID();
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return name;
    }
}
