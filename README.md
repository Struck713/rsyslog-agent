# syslog-agent

An open-source agent built for reading parsing Windows Event logs and sending them to a syslog server.
## Deployment

This project was written in Java 8, using Gradle for library management.

To build this project run
```bash
  ./gradle.bat jar
```
and then navigate to the `builds` folder (in `gradle/libs/`) and run
```bash
  java -jar SyslogAgent-1.0-SNAPSHOT.jar
```