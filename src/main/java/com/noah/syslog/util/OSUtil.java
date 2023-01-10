package com.noah.syslog.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OSUtil {

    public static enum Types {
        WINDOWS, MAC, UNIX, SOLARIS, UNKNOWN;
    }

    public static Types getType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return Types.WINDOWS;
        if (osName.contains("mac")) return Types.MAC;
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) return Types.UNIX;
        if (osName.contains("sunos")) return Types.SOLARIS;
        return Types.UNKNOWN;
    }

    public static String getName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

}
