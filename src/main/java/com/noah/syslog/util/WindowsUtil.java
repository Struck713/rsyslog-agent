package com.noah.syslog.util;

import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.message.enums.Severity;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class WindowsUtil {

    /**
     * THIS CLASS WAS MODIFIED FROM
     * com.sun.jna.platform.win32.Advapi32Util.EventLogIterator
     *
     * This is for consistently re-opening the Event Log when all the data
     * has been read from it.
     */
    public static class EventLogIterator implements Iterable<WindowsUtil.EventLogRecord>,
            Iterator<WindowsUtil.EventLogRecord> {

        private WinNT.HANDLE _h;
        private Memory _buffer = new Memory(1024 * 64); // memory buffer to
        // store events
        private boolean _done = false; // no more events
        private int _dwRead = 0; // number of bytes remaining in the current
        // buffer
        private Pointer _pevlr = null; // pointer to the current record
        private int _flags;
        private ConfigFilter _configFilter;

        public EventLogIterator(ConfigFilter configFilter) {
            _flags = WinNT.EVENTLOG_FORWARDS_READ;
            _configFilter = configFilter;
            this.open();
        }

        public void open() {
            _done = false;
            _dwRead = 0;
            _pevlr = null;
            _buffer.clear();

            _h = Advapi32.INSTANCE.OpenEventLog(null, _configFilter.getSource());
            if (_h == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }

        private boolean read() {
            // finished or bytes remain, don't read any new data
            if (_done || _dwRead > 0) {
                return false;
            }

            IntByReference pnBytesRead = new IntByReference();
            IntByReference pnMinNumberOfBytesNeeded = new IntByReference();

            if (!Advapi32.INSTANCE
                    .ReadEventLog(_h, WinNT.EVENTLOG_SEQUENTIAL_READ | _flags,
                            0, _buffer, (int) _buffer.size(), pnBytesRead,
                            pnMinNumberOfBytesNeeded)) {

                int rc = Kernel32.INSTANCE.GetLastError();

                // not enough bytes in the buffer, resize
                if (rc == W32Errors.ERROR_INSUFFICIENT_BUFFER) {
                    _buffer = new Memory(pnMinNumberOfBytesNeeded.getValue());

                    if (!Advapi32.INSTANCE.ReadEventLog(_h,
                            WinNT.EVENTLOG_SEQUENTIAL_READ | _flags, 0,
                            _buffer, (int) _buffer.size(), pnBytesRead,
                            pnMinNumberOfBytesNeeded)) {
                        throw new Win32Exception(
                                Kernel32.INSTANCE.GetLastError());
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

            _dwRead = pnBytesRead.getValue();
            _pevlr = _buffer;
            return true;
        }

        public void close() {
            _done = true;
            if (_h != null) {
                if (!Advapi32.INSTANCE.ClearEventLog(_h, null)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                if (!Advapi32.INSTANCE.CloseEventLog(_h)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                _h = null;
            }
        }

        @Override
        public Iterator<WindowsUtil.EventLogRecord> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            read();
            return !_done;
        }

        @Override
        public WindowsUtil.EventLogRecord next() {
            read();
            WindowsUtil.EventLogRecord record = new WindowsUtil.EventLogRecord(_pevlr);
            _dwRead -= record.getLength();
            _pevlr = _pevlr.share(record.getLength());
            return record;
        }

        @Override
        public void remove() {
        }

        public boolean filter(Advapi32Util.EventLogType type) {
            return !_configFilter.getLevels().contains(type);
        }

    }

    /**
     * THIS CLASS WAS MODIFIED FROM
     * com.sun.jna.platform.win32.Advapi32Util.EventLogRecord
     *
     * This is for consistently re-opening the Event Log when all the data
     * has been read from it.
     */
    public static class EventLogRecord {
        private WinNT.EVENTLOGRECORD _record;
        private String _source;
        private byte[] _data;
        private String[] _strings;

        /**
         * Raw record data.
         *
         * @return EVENTLOGRECORD.
         */
        public WinNT.EVENTLOGRECORD getRecord() {
            return _record;
        }

        /**
         * The Instance ID, a resource identifier that corresponds to a string
         * definition in the message resource file of the event source. The
         * Event ID is the Instance ID with the top two bits masked off.
         *
         * @return An integer representing the 32-bit Instance ID.
         */
        public int getInstanceId() {
            return _record.EventID.intValue();
        }

        /**
         * @deprecated As of 5.4.0, replaced by {@link #getInstanceId()}. The
         *             Event ID displayed in the Windows Event Viewer
         *             corresponds to {@link #getStatusCode()} for
         *             system-generated events.
         */
        @Deprecated
        public int getEventId() {
            return _record.EventID.intValue();
        }

        /**
         * Event source.
         *
         * @return String.
         */
        public String getSource() {
            return _source;
        }

        /**
         * Status code, the rightmost 16 bits of the Instance ID. Corresponds to
         * the Event ID field in the Windows Event Viewer for system-generated
         * events.
         *
         * @return An integer representing the low 16-bits of the Instance ID.
         */
        public int getStatusCode() {
            return _record.EventID.intValue() & 0xFFFF;
        }

        /**
         * Record number of the record. This value can be used with the
         * EVENTLOG_SEEK_READ flag in the ReadEventLog function to begin reading
         * at a specified record.
         *
         * @return Integer.
         */
        public int getRecordNumber() {
            return _record.RecordNumber.intValue();
        }

        public int getCategory() {
            return _record.EventCategory.intValue();
        }

        /**
         * Record length, with data.
         *
         * @return Number of bytes in the record including data.
         */
        public int getLength() {
            return _record.Length.intValue();
        }

        /**
         * Strings associated with this event.
         *
         * @return Array of strings or null.
         */
        public String[] getStrings() {
            return _strings;
        }

        /**
         * Event log type.
         *
         * @return Event log type.
         */
        public Advapi32Util.EventLogType getType() {
            switch (_record.EventType.intValue()) {
                case WinNT.EVENTLOG_SUCCESS:
                case WinNT.EVENTLOG_INFORMATION_TYPE:
                    return Advapi32Util.EventLogType.Informational;
                case WinNT.EVENTLOG_AUDIT_FAILURE:
                    return Advapi32Util.EventLogType.AuditFailure;
                case WinNT.EVENTLOG_AUDIT_SUCCESS:
                    return Advapi32Util.EventLogType.AuditSuccess;
                case WinNT.EVENTLOG_ERROR_TYPE:
                    return Advapi32Util.EventLogType.Error;
                case WinNT.EVENTLOG_WARNING_TYPE:
                    return Advapi32Util.EventLogType.Warning;
                default:
                    throw new RuntimeException("Invalid type: "
                            + _record.EventType.intValue());
            }
        }

        /**
         * Record timeGenerated, as java.util.Date
         *
         * @return Date object when record was generated
         */
        public Date getDate() {
            long millis = _record.TimeGenerated.longValue() * 1000L;
            return new Date(millis);
        }

        /**
         * Raw data associated with the record.
         *
         * @return Array of bytes or null.
         */
        public byte[] getData() {
            return _data;
        }

        public EventLogRecord(Pointer pevlr) {
            _record = new WinNT.EVENTLOGRECORD(pevlr);
            _source = pevlr.getWideString(_record.size());
            // data
            if (_record.DataLength.intValue() > 0) {
                _data = pevlr.getByteArray(_record.DataOffset.intValue(),
                        _record.DataLength.intValue());
            }
            // strings
            if (_record.NumStrings.intValue() > 0) {
                ArrayList<String> strings = new ArrayList<String>();
                int count = _record.NumStrings.intValue();
                long offset = _record.StringOffset.intValue();
                while (count > 0) {
                    String s = pevlr.getWideString(offset);
                    strings.add(s);
                    offset += s.length() * Native.WCHAR_SIZE;
                    offset += Native.WCHAR_SIZE;
                    count--;
                }
                _strings = strings.toArray(new String[0]);
            }
        }
    }

}
