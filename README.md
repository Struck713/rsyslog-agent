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

### Development

Syslog is based on the RFC5424 specification. Information on it can be found [here](  https://www.rfc-editor.org/rfc/rfc5424#section-6.1).