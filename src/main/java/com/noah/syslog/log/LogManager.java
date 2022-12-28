package com.noah.syslog.log;

import com.sun.jna.platform.win32.Advapi32Util;

public class LogManager {

    LogManager() {
        Advapi32Util.EventLogIterator iter = new Advapi32Util.EventLogIterator("Applications");
        int k = 0;
        while(iter.hasNext()) {
            Advapi32Util.EventLogRecord record = iter.next();
            System.out.println(record.getRecordNumber()
                    + ": Event ID: " + record.getEventId()
                    + ", Event Type: " + record.getType()
                    + ", Event Source: " + record.getSource());
        }
    }

}
