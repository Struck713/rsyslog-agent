package com.noah.syslog.log;

import java.util.List;

public interface LogAdapter {

    List<LogItem> next();


}
