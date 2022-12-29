package com.noah.syslog.log.filters;

import com.noah.syslog.message.Message;
import com.noah.syslog.util.WindowsUtil;
import com.sun.jna.platform.win32.Advapi32Util;

public interface Filter {

    String getName();
    String filter(String sourceName, WindowsUtil.EventLogRecord record);

}
