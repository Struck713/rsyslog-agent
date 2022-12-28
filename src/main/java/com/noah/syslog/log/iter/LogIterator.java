package com.noah.syslog.log.iter;

import com.noah.syslog.log.LogRecord;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.Iterator;

public class LogIterator implements Iterator<LogRecord>, Iterable<LogRecord> {

    private String sourceName;
    private WinNT.HANDLE handle = null;

    private Memory buffer = new Memory(1024 * 64);
    private int remainingBytes = 0;

    private Pointer pointerCurrentRecord = null;

    private int oldestRecord;
    private int lastReadRecord;

    public LogIterator(String sourceName) {
        this.sourceName = sourceName;
    }

    public void open() {
        this.handle = Advapi32.INSTANCE.OpenEventLog(null, this.sourceName);
        if (this.handle == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        IntByReference pointerOldest = new IntByReference();
        if (!Advapi32.INSTANCE.GetOldestEventLogRecord(this.handle, pointerOldest)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        this.oldestRecord = pointerOldest.getValue();
    }

    private boolean read() {
        // finished or bytes remain, don't read any new data
        if (this.remainingBytes > 0) {
            return false;
        }

        IntByReference pnBytesRead = new IntByReference();
        IntByReference pnMinNumberOfBytesNeeded = new IntByReference();

        if (! Advapi32.INSTANCE.ReadEventLog(this.handle,
                WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ,
                0, this.buffer, (int) this.buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {

            int rc = Kernel32.INSTANCE.GetLastError();

            // not enough bytes in the buffer, resize
            if (rc == W32Errors.ERROR_INSUFFICIENT_BUFFER) {
                this.buffer = new Memory(pnMinNumberOfBytesNeeded.getValue());

                if (! Advapi32.INSTANCE.ReadEventLog(this.handle,
                        WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ,
                        0, this.buffer, (int) this.buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            } else {
                // read failed, no more entries or error
                close();
                if (rc != W32Errors.ERROR_HANDLE_EOF) {
                    throw new Win32Exception(rc);
                }
                return false;
            }
        }

        this.remainingBytes = pnBytesRead.getValue();
        this.pointerCurrentRecord = this.buffer;
        return true;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public LogRecord next() {
        read();
        Advapi32Util.EventLogRecord record = new Advapi32Util.EventLogRecord(this.pointerCurrentRecord);
        this.oldestRecord++;
        this.remainingBytes -= record.getLength();
        this.pointerCurrentRecord = this.pointerCurrentRecord.share(record.getLength());
        return LogRecord.of(record);
    }

    @Override
    public Iterator<LogRecord> iterator() {
        return this;
    }

    public void close() {
        if (this.handle != null) {
            if (!Advapi32.INSTANCE.CloseEventLog(this.handle)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            this.handle = null;
        }
    }

}
