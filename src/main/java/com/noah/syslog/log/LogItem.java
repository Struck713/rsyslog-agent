package com.noah.syslog.log;

import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;

import java.util.Date;

public class LogItem {

    private Date date;
    private String application;
    private int processId;
    private int messageId;
    private Priority priority;
    private Severity severity;
    private String data;

    public LogItem(Date date, String application, int processId, int messageId, Priority priority, Severity severity, String data) {
        this.date = date;
        this.application = application;
        this.processId = processId;
        this.messageId = messageId;
        this.priority = priority;
        this.severity = severity;
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public String getApplication() {
        return application;
    }

    public int getProcessId() {
        return processId;
    }

    public int getMessageId() {
        return messageId;
    }

    public Priority getPriority() {
        return priority;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getData() {
        return data;
    }
}
