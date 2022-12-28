package com.noah.syslog.message;

import java.util.HashMap;

public class StructuredData extends HashMap<String, String> {

    private String header;

    public StructuredData() {
        this(0);
    }

    public StructuredData(int id) {
        this.header = "rsyslogAgent@" + id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append('[');
        builder.append(this.header);
        forEach((k, v) -> {
            builder.append(" " + k + "=\"" + v + "\"");
        });
        builder.append(']');

        return builder.toString();
    }
}
