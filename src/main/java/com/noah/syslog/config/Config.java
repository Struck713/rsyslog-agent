package com.noah.syslog.config;

import java.util.List;

public class Config {

    private long timeBetweenReads;
    private ConfigHost host;
    private List<ConfigFilter> filters;

    public long getTimeBetweenReads() {
        return this.timeBetweenReads;
    }

    public ConfigHost getHost() { return this.host; }
    public ConfigFilter getFilter(String source) {
        return this.filters.stream()
                .filter(filter -> filter.getSource().equalsIgnoreCase(source))
                .findFirst()
                .orElse(null);
    }

    public List<ConfigFilter> getFilters() {
        return this.filters;
    }
}
