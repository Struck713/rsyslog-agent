package com.noah.syslog.log;

import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.log.filters.Filter;
import com.noah.syslog.util.WindowsUtil;
import com.sun.jna.platform.win32.Advapi32Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogManager {

    private List<WindowsUtil.EventLogIterator> iterators;
    private List<Filter> filters;

    public LogManager(List<String> sources, List<ConfigFilter> filters) {
        this.iterators = sources.stream().map(WindowsUtil.EventLogIterator::new).collect(Collectors.toList());
        this.filters = filters.stream().map(Filter::of).collect(Collectors.toList());
    }

    public List<WindowsUtil.EventLogRecord> next() {
        List<WindowsUtil.EventLogRecord> records = new ArrayList<>();
        for (WindowsUtil.EventLogIterator iterator : this.iterators) {
            if (!iterator.hasNext()) {
                iterator.open();
                continue;
            }

            while (iterator.hasNext()) {
                WindowsUtil.EventLogRecord next = iterator.next();
                if (this.filters.stream().anyMatch(filter -> filter.filter(next))) continue;
                records.add(next);
            }
        }
        return records;
    }

}
