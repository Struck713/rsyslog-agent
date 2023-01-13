package com.noah.syslog.log.parser;

import com.noah.syslog.log.LogItem;

public interface Parser {

    Parser APACHE = new ApacheParser();

    static Parser of(String name) {
        switch (name) {
            case "Apache": return Parser.APACHE;
            default: return null;
        }
    }

    LogItem parse(String line);
    String getName();

}
