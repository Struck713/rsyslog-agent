package com.noah.syslog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noah.syslog.client.Client;
import com.noah.syslog.client.UDPClient;
import com.noah.syslog.config.Config;
import com.noah.syslog.config.ConfigHost;
import com.noah.syslog.log.LogItem;
import com.noah.syslog.log.LogAdapter;
import com.noah.syslog.log.adapter.LinuxAdapter;
import com.noah.syslog.log.adapter.WindowsAdapter;
import com.noah.syslog.message.Message;
import com.noah.syslog.message.enums.Encodings;
import com.noah.syslog.util.FileUtil;
import com.noah.syslog.util.OSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class SyslogAgent {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = LoggerFactory.getLogger("SyslogAgent");
    public static final File CONFIG_FILE = new File("config.json");

    public static void main(String[] args) throws IOException, InterruptedException {
        Config config = SyslogAgent.loadConfig();
        final long timeBetweenReads = config.getTimeBetweenReads();

        LogAdapter logAdapter;

        SyslogAgent.LOGGER.info("Detecting operating system..");
        OSUtil.Types osType = OSUtil.getType();
        if (osType == OSUtil.Types.WINDOWS) logAdapter = new WindowsAdapter(config.getSources(), config.getFilters());
        else if (osType == OSUtil.Types.UNIX) logAdapter = new LinuxAdapter(timeBetweenReads, config.getSources(), config.getFilters());
        else { SyslogAgent.LOGGER.info("Operating system, " + osType.name() + " is unsupported!"); return; }
        SyslogAgent.LOGGER.info("Detected operating system: " + osType.name());

        SyslogAgent.LOGGER.info("Initializing SyslogAgent..");
        ConfigHost host = config.getHost();
        final String hostname = OSUtil.getName();
        Client client = new UDPClient(host.getInetAddress(), host.getPort());
        SyslogAgent.LOGGER.info("Sending " + host.getProtocol() + " messages on " + host.getAddress() + ":" + host.getPort() + " hostname, " + hostname);

        while (true) {
            List<LogItem> nextRecords = logAdapter.next();
            if (nextRecords.isEmpty()) continue;

            nextRecords.stream().map(record ->
                new Message(
                        record.getPriority().with(record.getSeverity()),
                        record.getDate(),
                        hostname,
                        record.getApplication(),
                        record.getProcessId(),
                        record.getMessageId(),
                        Encodings.ANY,
                        record.getData()
                )
            ).forEach(message -> {
                client.send(message);
                SyslogAgent.LOGGER.info(message.toString());
            });

            Thread.sleep(timeBetweenReads);
        }

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