package com.noah.syslog.log.adapter;

import com.google.gson.reflect.TypeToken;
import com.noah.syslog.SyslogAgent;
import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.log.LogAdapter;
import com.noah.syslog.log.LogItem;
import com.noah.syslog.log.filters.Filter;
import com.noah.syslog.log.watcher.FileWatcher;
import com.noah.syslog.log.watcher.FileWatcherListener;
import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;
import com.noah.syslog.util.FileUtil;
import com.noah.syslog.util.StringUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class LinuxAdapter implements LogAdapter, FileWatcherListener {

    public static final SimpleDateFormat APACHE_DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ssZ");
    public static final File STATES = new File("states.json");

    private FileWatcher watcher;
    private Map<File, Long> fileStates;
    private List<String> items;
    private List<Filter> filters;

    public LinuxAdapter(long interval, List<String> sources, List<ConfigFilter> filters) {
        this.watcher = new FileWatcher(interval, this);
        this.filters = filters.stream().map(Filter::of).collect(Collectors.toList());
        this.items = new ArrayList<>();

        this.fileStates = new HashMap<>();
        if (LinuxAdapter.STATES.exists()) {
            String rawJson = FileUtil.read(LinuxAdapter.STATES);
            Type mapType = new TypeToken<Map<String, Long>>() {}.getType();
            Map<String, Long> stringsMap = SyslogAgent.GSON.fromJson(rawJson, mapType);
            stringsMap.forEach((k, v) -> this.fileStates.put(new File(k), v));
        }

        sources.stream()
            .map(File::new)
            .filter(file -> {
                boolean exists = file.exists();
                if (!exists) SyslogAgent.LOGGER.info("Source log file, '" + file.getAbsolutePath() + "' does not exist.");
                return exists;
            })
            .forEach(this.watcher::watch);

        this.watcher.start();
    }

    private int messageId = 0;

    @Override
    public List<LogItem> next() {
        // 185.180.143.8 - - [10/Jan/2023:01:29:35 -0500] "GET / HTTP/1.1" 200 15 "-" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
        List<LogItem> logItems = new ArrayList<>();
        this.items.stream()
            .filter(item -> item.contains("404"))
            .map(item -> item.split(" "))
            .forEach(list -> {
                String ip = list[0];
                String request = StringUtil.join(" ", list[5], list[6], list[7]);
                String datePart1 = list[3];
                String datePart2 = list[4];
                String dateString = datePart1.substring(1) + datePart2.substring(0, datePart2.length() - 1);
                Date date = null;
                try {
                    date = APACHE_DATE_FORMAT.parse(dateString);
                } catch (ParseException e) {
                    SyslogAgent.LOGGER.error("Failed to parse date: " + dateString);
                }

                logItems.add(new LogItem(
                        date,
                        "Tomcat",
                        1,
                        messageId++,
                        Priority.LOG_ALERT,
                        Severity.ERROR,
                        "Tomcat threw a 404: " + ip + " submitted the request " + request)
                );
            });
        this.items.clear();

        return logItems;
    }

    @Override
    public void onFileChange(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            long state = this.fileStates.getOrDefault(file, 0L);
            AtomicLong read = new AtomicLong(0L);
            reader.lines().skip(state).forEach(string -> {
                this.items.add(string);
                read.getAndIncrement();
            });
            long newState = read.get();
            this.fileStates.put(file, state + newState);

            SyslogAgent.LOGGER.info("Read " + newState + " new lines from: " + file.getAbsolutePath());
            String rawStates = SyslogAgent.GSON.toJson(this.fileStates);
            FileUtil.write(rawStates, LinuxAdapter.STATES);
        } catch (IOException e) {
            SyslogAgent.LOGGER.error("Failed to read from file: " + file.getName());
        }
    }
}
