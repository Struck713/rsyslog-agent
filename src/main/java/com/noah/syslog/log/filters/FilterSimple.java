package com.noah.syslog.log.filters;

import com.noah.syslog.util.WindowsUtil;
import com.sun.jna.platform.win32.Advapi32Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FilterSimple implements Filter {

    private String source;
    private List<Advapi32Util.EventLogType> levels;

    public FilterSimple(String source, Map<String, Object> options) {
        this.source = source;

        if (!options.containsKey("levels")) throw new RuntimeException("Invalid options for Simple filter");
        List<String> types = (List<String>)options.get("levels");
        this.levels = types.stream().map(Advapi32Util.EventLogType::valueOf).collect(Collectors.toList());
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public boolean filter(WindowsUtil.EventLogRecord record) {
        Advapi32Util.EventLogType type = record.getType();
        return !this.levels.contains(type);
    }
}
