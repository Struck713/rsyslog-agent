package com.noah.syslog.log.watcher;

import java.io.File;

public interface FileWatcherListener {

    void onFileChange(File file);

}
