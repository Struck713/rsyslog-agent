package com.noah.syslog.log;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;

import java.util.Arrays;

public class LogRecord {

    public static LogRecord of(Advapi32Util.EventLogRecord record) {
        return new LogRecord(record.getRecordNumber(), record.getStatusCode(), record.getSource(), record.getData()); // TODO IMPLEMENT
    }

    private WinNT.EVENTLOGRECORD _record = null;
    private int recordId;
    private int eventId;
    private String source;
    private byte[] data;

    public LogRecord(int recordId, int eventId, String source, byte[] data) {
        this.recordId = recordId;
        this.eventId = eventId;
        this.source = source;
        this.data = data;
    }

    public int getRecordId() {
        return recordId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getSource() {
        return source;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
                "recordId=" + recordId +
                ", eventId=" + eventId +
                ", source='" + source + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
