package com.noah.syslog.message;

import com.noah.syslog.message.enums.Encodings;
import com.noah.syslog.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSSXXX");
    public static final String NIL_VALUE = "-";

    private int priority;
    private int version;
    private Date timestamp;
    private String hostname;
    private String application;
    private int processId;
    private int messageId;

    private StructuredData structuredData;

    private Encodings encoding;
    private String message;

    public Message(int priority,
                   Date timestamp,
                   String hostname,
                   String application,
                   int processId,
                   int messageId,
                   StructuredData structuredData,
                   Encodings encoding,
                   String message) {
        this.priority = priority;
        this.version = 1;
        this.timestamp = timestamp;
        this.hostname = hostname;
        this.application = application;
        this.processId = processId;
        this.messageId = messageId;
        this.structuredData = structuredData;
        this.encoding = encoding;
        this.message = message;
    }

    public Message(int priority,
                   Date timestamp,
                   String hostname,
                   String application,
                   int processId,
                   int messageId,
                   Encodings encoding,
                   String message) {
        this(priority, timestamp, hostname, application, processId, messageId, null, encoding, message);
    }

    @Override
    public String toString() {
        return StringUtil.join(" ",
                "<" + this.priority + ">" + this.version,
                DATE_FORMAT.format(this.timestamp),
                this.hostname,
                this.application,
                Integer.toString(this.processId),
                Integer.toString(this.messageId),
                this.structuredData == null ? NIL_VALUE : this.structuredData.toString(),
                this.encoding.use(this.message)
        );
    }
}
