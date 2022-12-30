package com.noah.syslog.config;

import java.util.Map;

public class ConfigFilter {

    private String source;
    private String filter;
    private Map<String, Object> options;

    public String getSource() {
        return source;
    }

    public String getFilter() {
        return filter;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return this.filter;
    }

}
