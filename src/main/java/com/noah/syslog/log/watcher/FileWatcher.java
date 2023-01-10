package com.noah.syslog.log.watcher;

import com.noah.syslog.SyslogAgent;
import com.noah.syslog.log.adapter.LinuxAdapter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class FileWatcher {

    private FileAlterationMonitor monitor;
    private FileWatcherListener listener;

    public FileWatcher(long interval, FileWatcherListener listener) {
        this.monitor = new FileAlterationMonitor(interval);
        this.listener = listener;
    }

    public void watch(File file) {
        FileAlterationObserver observer = new FileAlterationObserver(file);
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                listener.onFileChange(file);
            }
        });
        this.monitor.addObserver(observer);
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
