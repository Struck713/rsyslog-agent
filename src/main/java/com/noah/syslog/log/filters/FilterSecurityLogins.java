package com.noah.syslog.log.filters;

import com.noah.syslog.util.OSUtil;
import com.noah.syslog.util.WindowsUtil;
import com.sun.jna.platform.win32.Advapi32Util;

public class FilterSecurityLogins implements Filter<WindowsUtil.EventLogRecord> {

    public static final int LOGON_EVENT_ID = 4624;
    public static final int LOGOFF_EVENT_ID = 4634;

    private String source;

    FilterSecurityLogins(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public OSUtil.Types getSupportedOS() {
        return OSUtil.Types.WINDOWS;
    }

    @Override
    public boolean filter(WindowsUtil.EventLogRecord record) {
        if (record.getType() != Advapi32Util.EventLogType.AuditSuccess) return true;

        int eventId = record.getStatusCode();
        if (!(eventId == LOGOFF_EVENT_ID || eventId == LOGON_EVENT_ID)) return true;

        String[] strings = record.getStrings();
        if (strings == null) return true;

        String username = strings[1]; //username on login
        String workstationName = strings[2];
        return false;
    }

}
