# syslog-agent

An open-source agent built for reading parsing Windows Event logs and sending them to a syslog server.
## About

A little while ago, I wanted to build a centralized logging server that would collect
and process all of the logs from Windows and Linux servers. [Rsyslog](https://www.rsyslog.com/)
seems to be one of the best solutions when it comes to this.

They have a Windows Agent on their site, but it's not like the Linux version of the program. I wanted
to make something like that for Windows, so this is what I came up with.

When building this, I wanted it to be:
- Fast
- Lightweight
- Configurable
- Open-source

I believe that I have achieved those goals.
## Installation

This project was written and tested using Java 8, you will need to have it installed if you want
to run the project. It might work on other versions of Java but **I cannot guarantee that**.

There are two ways you can get the jar file, you can:
- Download the latest build from the [releases page](https://github.com/Struck713/syslog-agent/releases).
- Build the jar file yourself, clone the repository and run: `gradlew.bat shadowJar`

Once you build or download the jar file, you can run it using:
```bash
    java -jar SyslogAgent.jar
```
## Configuration

Upon startup, a `config.json` file will be created in the folder with the jar file.
It should look something like this:
```JSON
{
  "timeBetweenReads": 10000,
  "host": {
    "protocol": "UDP",
    "address": "127.0.0.1",
    "port": 514
  },
  "sources": [ "Application", "Security" ],
  "filters": [
    {
      "source": "Application",
      "filter": "Simple",
      "options": {
        "levels": [ "Warning", "Error" ]
      }
    },
    {
      "source": "Security",
      "filter": "Security Logins",
      "options": {}
    }
  ]
}
```

Here is an explaination of what is going on:
- `timeBetweenReads` is how frequent we access the Event Logs (in milliseconds)
- `host` is the syslog server that messages will be forwarded to:
    - `UDP` is the only protocol currently supported
    - `TCP` will be supported in the future (hopefully)
- `sources` are the Event Logs we want to read
- `filters` is a list of filters:
    - `source` is one of the sources we specified
    - `filter` is the type of filter (See the list of filters below)
    - `options` are the options for that filter

Filter types:
- `Simple` will filter based on the provided logging levels
- `Security Logins` will send messages when a user logs in or logs out


## Authors

- [@Struck713](https://github.com/Struck713/syslog-agent)

