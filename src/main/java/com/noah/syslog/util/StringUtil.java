package com.noah.syslog.util;

public class StringUtil {

    public static String join(String delim, String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) builder.append(string + delim);
        return builder.substring(0, builder.length() - delim.length());
    }

    public static int countOctets(String string) {
        return string.getBytes().length; // octet is 8 bits, a byte is 8 bits. byte count is the same as octet count
    }

}
