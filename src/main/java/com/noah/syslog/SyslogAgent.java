package com.noah.syslog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noah.syslog.config.Config;
import com.noah.syslog.config.ConfigHost;
import com.noah.syslog.config.ConfigLog;
import com.noah.syslog.message.Message;
import com.noah.syslog.message.enums.Encodings;
import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;
import com.noah.syslog.util.FileUtil;

import java.io.*;
import java.util.Date;
import java.util.stream.Collectors;

public class SyslogAgent {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File CONFIG_FILE = new File("config.json");

    public static void main(String[] args) throws IOException {
        Config config = SyslogAgent.loadConfig();

        ConfigHost host = config.getHost();
        SyslogClient client = new SyslogClient(host.getInetAddress(), host.getPort());

        //ConfigLog log = config.getLog();
        //LogManager logManager = new LogManager(log);

        Message message = new Message(
                Priority.MAIL.with(Severity.ALERT),
                new Date(),
                "noah.com",
                "noah",
                1,
                32001,
                //structuredData,
                Encodings.ANY,
                "hello, from noah's external server!"
        );
        client.send(message);
        System.out.println("Sent: " + message);
        client.close();

//        while (true) {
//            List<LogRecord> nextRecords = logManager.next();
//            if (nextRecords.isEmpty()) continue;
//
//            nextRecords.forEach(System.out::println);
//        }

        //client.close();
    }

    public static Config loadConfig() throws IOException {
        if (!CONFIG_FILE.exists()) {
            InputStream configStream = SyslogAgent.class
                    .getClassLoader()
                    .getResourceAsStream("config.json");
            InputStreamReader isReader = new InputStreamReader(configStream);
            BufferedReader reader = new BufferedReader(isReader);
            String rawData = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            isReader.close();
            reader.close();

            FileUtil.write(rawData, CONFIG_FILE);
        }

        String rawData = FileUtil.read(CONFIG_FILE);
        return SyslogAgent.GSON.fromJson(rawData, Config.class);
    }

}