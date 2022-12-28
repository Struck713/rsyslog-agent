package com.noah.syslog.log;

import com.noah.syslog.config.ConfigLog;
import com.sun.jna.platform.win32.Advapi32Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class LogManager {

    private ConfigLog config;
    private List<Advapi32Util.EventLogIterator> iterators;

    public LogManager(ConfigLog config) {
        this.config = config;

        this.iterators = new ArrayList<>();
        this.config.getTypes().forEach(type -> iterators.add(new Advapi32Util.EventLogIterator(type)));
    }

    public List<LogRecord> next() {
        return this.iterators.stream()
                .filter(Iterator::hasNext)
                .map(Iterator::next)
                .filter(record -> this.config.getLevels().contains(record.getType()))
                .map(LogRecord::of)
                .collect(Collectors.toList());
    }

}
