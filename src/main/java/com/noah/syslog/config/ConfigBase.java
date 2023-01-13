package com.noah.syslog.config;

import java.util.List;
import java.util.Map;

public class ConfigBase {

    private long timeBetweenReads;
    private ConfigHost host;
    private Map<String, String> sources;
    private List<ConfigFilter> filters;

    public long getTimeBetweenReads() {
        return this.timeBetweenReads;
    }

    public ConfigHost getHost() { return this.host; }

    public Map<String, String> getSources() {
        return this.sources;
    }

    public List<ConfigFilter> getFilters() {
        return this.filters;
    }
}
