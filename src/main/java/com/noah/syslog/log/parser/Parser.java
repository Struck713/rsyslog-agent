package com.noah.syslog.log.parser;

import com.noah.syslog.log.LogItem;

public interface Parser {

    Parser APACHE = new ApacheParser();
    Parser TOMCAT = new TomcatParser();

    static Parser of(String name) {
        switch (name) {
            case "tomcat": return Parser.TOMCAT;
            case "apache": return Parser.APACHE;
            default: return null;
        }
    }

    LogItem parse(String line);
    String getName();

}
