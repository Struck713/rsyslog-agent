package com.noah.syslog.config;

import java.util.List;

public class Config {

    private long timeBetweenReads;
    private ConfigHost host;
    private List<String> sources;
    private List<ConfigFilter> filters;

    public long getTimeBetweenReads() {
        return this.timeBetweenReads;
    }

    public ConfigHost getHost() { return this.host; }

    public List<String> getSources() {
        return this.sources;
    }

    public List<ConfigFilter> getFilters() {
        return this.filters;
    }
}
