package com.noah.syslog.log.filters;

import com.noah.syslog.util.OSUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterMessageContains implements Filter<String> {

    private String source;
    private List<String> regex;

    public FilterMessageContains(String source, Map<String, Object> options) {
        this.source = source;
        this.regex = new ArrayList<>();
        options.values().forEach(o -> regex.add((String) o));
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public OSUtil.Types getSupportedOS() {
        return OSUtil.Types.UNIX;
    }

    @Override
    public boolean filter(String item) {
        return !this.regex.stream().anyMatch(regex -> item.contains(regex));
    }
}
