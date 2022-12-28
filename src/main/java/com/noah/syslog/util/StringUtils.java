package com.noah.syslog.util;

public class StringUtils {

    public static String join(String delim, String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) builder.append(string + delim);
        return builder.substring(0, builder.length() - delim.length());
    }

}
