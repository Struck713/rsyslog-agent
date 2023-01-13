package com.noah.syslog.log.adapter;

import com.google.gson.reflect.TypeToken;
import com.noah.syslog.SyslogAgent;
import com.noah.syslog.config.ConfigFilter;
import com.noah.syslog.log.LogAdapter;
import com.noah.syslog.log.LogItem;
import com.noah.syslog.log.filters.Filter;
import com.noah.syslog.log.parser.ApacheParser;
import com.noah.syslog.log.parser.Parser;
import com.noah.syslog.log.watcher.FileWatcher;
import com.noah.syslog.log.watcher.FileWatcherListener;
import com.noah.syslog.message.enums.Priority;
import com.noah.syslog.message.enums.Severity;
import com.noah.syslog.util.*;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class LinuxAdapter implements LogAdapter, FileWatcherListener {

    public static final File STATES = new File("states.json");

    private final Parser parser = new ApacheParser();
    private FileWatcher watcher;
    private Map<File, Long> fileStates;
    private List<Pair<String, String>> items;
    private List<Filter> filters;

    public LinuxAdapter(long interval, Map<String, String> sources, List<ConfigFilter> filters) {
        this.watcher = new FileWatcher(interval, this);
        this.filters = filters.stream()
                .map(Filter::of)
                .filter(type -> type.getSupportedOS() == OSUtil.Types.UNIX)
                .collect(Collectors.toList());
        this.items = new ArrayList<>();

        this.fileStates = new HashMap<>();
        if (LinuxAdapter.STATES.exists()) {
            String rawJson = FileUtil.read(LinuxAdapter.STATES);
            Type mapType = new TypeToken<Map<String, Long>>() {}.getType();
            Map<String, Long> stringsMap = SyslogAgent.GSON.fromJson(rawJson, mapType);
            stringsMap.forEach((k, v) -> this.fileStates.put(new File(k), v));
        }

        sources.forEach((k, v) -> {
            File file = new File(k);
            if (!file.exists()) {
                SyslogAgent.LOGGER.info("Source log file, '" + file.getAbsolutePath() + "' does not exist.");
                return;
            }

            Parser type = Parser.of(v);
            if (type == null) {
                SyslogAgent.LOGGER.info("Parser, " + v + " for log file, '" + file.getAbsolutePath() + "' does not exist.");
                return;
            }

            this.watcher.watch(file);
        });

        this.watcher.start();
    }

    public boolean checkFilters(String source, String item) {
        return this.filters.stream()
                .filter(filter -> filter.getSource().equals(source))
                .anyMatch(filter -> filter.filter(item));
    }

    @Override
    public List<LogItem> next() {
        // 185.180.143.8 - - [10/Jan/2023:01:29:35 -0500] "GET / HTTP/1.1" 200 15 "-" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"
        List<LogItem> logItems = new ArrayList<>();
        this.items.stream()
                .filter(item -> this.checkFilters(item.getLeft(), item.getRight()))
                .map(Pair::getRight)
                .map(this.parser::parse)
                .forEach(logItems::add);
        this.items.clear();

        return logItems;
    }

    @Override
    public void onFileChange(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            long state = this.fileStates.getOrDefault(file, 0L);
            AtomicLong read = new AtomicLong(0L);
            reader.lines().skip(state).forEach(string -> {
                this.items.add(Pair.of(file.getName(), string));
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
