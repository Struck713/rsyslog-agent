package com.noah.syslog.log.adapter;

import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.log.LogItem;
import com.noah.syslog.log.LogAdapter;
import com.noah.syslog.log.filters.Filter;
import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;
import com.noah.syslog.util.WindowsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WindowsAdapter implements LogAdapter {

    private List<WindowsUtil.EventLogIterator> iterators;
    private List<Filter> filters;

    public WindowsAdapter(List<String> sources, List<ConfigFilter> filters) {
        this.iterators = sources.stream().map(WindowsUtil.EventLogIterator::new).collect(Collectors.toList());
        this.filters = filters.stream().map(Filter::of).collect(Collectors.toList());
    }

    public List<LogItem> next() {
        List<LogItem> records = new ArrayList<>();
        for (WindowsUtil.EventLogIterator iterator : this.iterators) {
            if (!iterator.hasNext()) {
                iterator.open();
                continue;
            }

            while (iterator.hasNext()) {
                WindowsUtil.EventLogRecord next = iterator.next();
                if (this.checkFilters(iterator, next)) continue;

                String data = next.getStrings() == null ? "No data attached" : Arrays.toString(next.getStrings());
                String source = next.getSource();
                Severity severity = Severity.of(next.getType());
                int statusCode = next.getStatusCode();

                String message = severity.getName() + " from " + source + " with status code " + statusCode + ": " + data;
                records.add(new LogItem(
                        next.getDate(),
                        source,
                        statusCode,
                        next.getRecordNumber(),
                        Priority.LOG_ALERT,
                        severity,
                        message
                ));
            }
        }
        return records;
    }

    public boolean checkFilters(WindowsUtil.EventLogIterator iterator, WindowsUtil.EventLogRecord record) {
        return this.filters.stream()
                .filter(filter -> filter.getSource().equalsIgnoreCase(iterator.getSource()))
                .anyMatch(filter -> filter.filter(record));
    }

}
