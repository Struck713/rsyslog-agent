package com.noah.syslog.log.filters;

import com.noah.syslog.message.Message;
import com.noah.syslog.util.WindowsUtil;
import com.sun.jna.platform.win32.Advapi32Util;

import java.util.Arrays;

public class FilterSecurityLogins implements Filter {

    @Override
    public String getName() {
        return "UserLogin";
    }

    @Override
    public String filter(String sourceName, WindowsUtil.EventLogRecord record) {
        if (sourceName.equals("Security")) return null;
        if (record.getType() != Advapi32Util.EventLogType.AuditSuccess) return null;

        String[] strings = record.getStrings();
        if (strings == null) return null;

        String username = strings[1]; //username on login
        String workstationName = strings[2];
        return username + " logged in to " + workstationName + ".";
    }

}
