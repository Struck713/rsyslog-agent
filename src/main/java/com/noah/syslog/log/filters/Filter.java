package com.noah.syslog.log.filters;

import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.util.WindowsUtil;

import java.util.Map;

public interface Filter {

    static Filter of(ConfigFilter filter) {
        String source = filter.getSource();
        String filterName = filter.getFilter();
        switch (filterName) {
            case "Simple": return new FilterSimple(source, filter.getOptions());
            case "Security Logins": return new FilterSecurityLogins(source);
            default: throw new RuntimeException("Unknown filter: " + filterName);
        }
    }

    boolean filter(WindowsUtil.EventLogRecord record);

}
