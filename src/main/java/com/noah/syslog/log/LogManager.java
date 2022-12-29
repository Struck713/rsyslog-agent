package com.noah.syslog.log;

import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.util.WindowsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogManager {

    private List<WindowsUtil.EventLogIterator> iterators;

    public LogManager(List<ConfigFilter> filters) {
        this.iterators = new ArrayList<>();
        filters.stream().map(WindowsUtil.EventLogIterator::new).forEach(iterators::add);
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
                if (iterator.filter(next.getType())) continue;
                records.add(next);
            }
        }
        return records;
    }

}
