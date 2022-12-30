package com.noah.syslog.log.filters;

import com.noah.syslog.util.WindowsUtil;
import com.sun.jna.platform.win32.Advapi32Util;

import java.util.Map;

public class FilterSecurityLogins implements Filter {

    private String source;

    FilterSecurityLogins(String source) {
        this.source = source;
    }

    @Override
    public boolean filter(WindowsUtil.EventLogRecord record) {
        if (this.source.equals("Security")) return true;
        if (record.getType() != Advapi32Util.EventLogType.AuditSuccess) return true;

        String[] strings = record.getStrings();
        if (strings == null) return true;

        String username = strings[1]; //username on login
        String workstationName = strings[2];
        return false;
    }

}
