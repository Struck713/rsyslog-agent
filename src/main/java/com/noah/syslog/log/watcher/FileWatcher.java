package com.noah.syslog.log.watcher;

import com.noah.syslog.SyslogAgent;
import com.noah.syslog.log.adapter.LinuxAdapter;
import com.noah.syslog.log.filters.Filter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;

public class FileWatcher {

    private FileAlterationMonitor monitor;
    private FileWatcherListener listener;

    public FileWatcher(long interval, FileWatcherListener listener) {
        this.monitor = new FileAlterationMonitor(interval);
        this.listener = listener;
    }

    public void watch(File file) {
        SyslogAgent.LOGGER.info("File watcher was started for: " + file.getAbsolutePath());
        File directory = file.getParentFile();
        FileAlterationObserver observer = new FileAlterationObserver(directory, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contentEquals(file.getName());
            }
        });

        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                listener.onFileChange(file);
            }
        });

        this.monitor.addObserver(observer);
        this.listener.onFileChange(file);
    }

    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            SyslogAgent.LOGGER.info("Could not stop file monitor: " + e.getMessage());
        }
    }

    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            SyslogAgent.LOGGER.info("Could not start file monitor: " + e.getMessage());
        }
    }

}
