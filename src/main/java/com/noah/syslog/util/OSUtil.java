package com.noah.syslog.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OSUtil {

    public static String getName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

}
