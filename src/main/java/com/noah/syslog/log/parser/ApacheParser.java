package com.noah.syslog.log.parser;

import com.noah.syslog.SyslogAgent;
import com.noah.syslog.log.LogItem;
import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;
import com.noah.syslog.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApacheParser implements Parser {

    public static final SimpleDateFormat APACHE_DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ssZ");

    private int messageId;

    public ApacheParser() {
        this.messageId = 0;
    }

    @Override
    public LogItem parse(String line) {
        String[] items = line.split(" ");

        String ip = items[0];
        String request = StringUtil.join(" ", items[5], items[6], items[7]);
        String statusCode = items[8];
        String datePart1 = items[3];
        String datePart2 = items[4];
        String dateString = datePart1.substring(1) + datePart2.substring(0, datePart2.length() - 1);
        Date date = null;
        try {
            date = APACHE_DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            SyslogAgent.LOGGER.error("Failed to parse date: " + dateString);
        }

        return new LogItem(
                date,
                this.getName(),
                1,
                messageId++,
                Priority.LOG_ALERT,
                Severity.ERROR,
                statusCode + ": " + ip + " submitted the request " + request
        );
    }

    @Override
    public String getName() {
        return "apache";
    }

}
