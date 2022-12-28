package com.noah.syslog;

import com.noah.syslog.message.Message;
import com.noah.syslog.message.StructuredData;
import com.noah.syslog.message.enums.Encodings;
import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class SyslogAgent {

    public static void main(String[] args) throws IOException {
        StructuredData structuredData = new StructuredData();
        structuredData.put("test", "some_data");
        structuredData.put("options", "some_other_data");

        System.out.println(structuredData);

        SyslogClient client = new SyslogClient(InetAddress.getLocalHost(), 5555);
        for (int i = 0; i < 10; i++) {
            Message message = new Message(
                    Priority.MAIL.with(Severity.ALERT),
                    new Date(),
                    "myserver.com",
                    "su",
                    i,
                    32001,
                    //structuredData,
                    Encodings.ANY,
                    "'su root' failed in process " + i
            );
            client.send(message);
        }
        client.close();
    }

}