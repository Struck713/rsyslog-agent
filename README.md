# syslog-agent

An open-source agent built for reading parsing Windows Event logs and sending them to a syslog server.
## Deployment

This project was written in Java 8, using Gradle for library management.

To build this project:
```bash
  ./gradle.bat shadowJar
```

After building, you can run the project with:
```bash
  java -jar SyslogAgent.jar
```