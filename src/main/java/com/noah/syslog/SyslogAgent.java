package com.noah.syslog;

import com.noah.syslog.message.Message;
import com.noah.syslog.message.StructuredData;
import com.noah.syslog.message.enums.Encodings;
import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

public class SyslogAgent {

    public static void main(String[] args) throws IOException {
        StructuredData structuredData = new StructuredData();
        structuredData.put("test", "some_data");
        structuredData.put("options", "some_other_data");

        System.out.println(structuredData);

        Message message = new Message(
                Priority.NTP.with(Severity.WARNING),
                new Date(),
                "myserver.com",
                "su",
                201,
                32001,
                //structuredData,
                Encodings.UTF_8,
                "'su root' failed on /dev/pts/7"
        );

        SyslogClient client = new SyslogClient(InetAddress.getLocalHost(), 5555);
        client.send(message);

        System.out.println(message);
    }

}