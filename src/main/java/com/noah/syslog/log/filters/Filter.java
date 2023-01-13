package com.noah.syslog.log.filters;

import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.log.LogItem;
import com.noah.syslog.util.OSUtil;
import com.noah.syslog.util.WindowsUtil;

public interface Filter<T> {

    static Filter of(ConfigFilter filter) {
        String source = filter.getSource();
        String filterName = filter.getFilter();
        switch (filterName) {
            case "Simple": return new FilterSimple(source, filter.getOptions());
            case "Security Logins": return new FilterSecurityLogins(source);
            case "Message Contains": return new FilterMessageContains(source, filter.getOptions());
            default: throw new RuntimeException("Unknown filter: " + filterName);
        }
    }

    String getSource();
    OSUtil.Types getSupportedOS();
    boolean filter(T item);

}
