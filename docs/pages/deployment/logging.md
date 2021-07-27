---
layout: default
title: Logging
nav_order: 2
description: ""
permalink: /Deployment/Configuration/Logging
parent: Configuration
grand_parent: Deployment
---

# Logging
{: .fs-9 }

Here, you can find a detailed description on how to use built-in logging functionality.
{: .fs-6 .fw-300 }

---

The Dataspace Connector provides multiple ways for logging and accessing information.

## Static Configuration

You may configure logging setting in the `log4j2.xml` at `src/main/resources`. There, you will find
the different loggers and the target outputs used within the Dataspace Connector.

To change the logging level of the Dataspace Connector, modify the attribute `level` of the logger
named `io.dataspaceconnector`. The different values of logging level can be found
[here](https://logging.apache.org/log4j/2.x/manual/configuration.html#SystemProperties).

```xml
<Logger name="io.dataspaceconnector" level="info">
    <AppenderRef ref="ConsoleAppender"/>
</Logger>
```

The `AppenderRef` of the logger controls the output of the log. Add or remove elements of type
`AppenderRef` to add additional outputs or remove existing ones.

The Dataspace Connector offers preconfigured appenders. For logging to console use `ConsoleAppender`
or to file use `RollingFile` as values for the `ref` attribute.

```xml
<Logger name="io.dataspaceconnector" level="debug">
    <AppenderRef ref="ConsoleAppender"/>
    <AppenderRef ref="RollingFile"/>
</Logger>
```

To add additional logging outputs or change the logging format consult
[here](https://logging.apache.org/log4j/2.x/manual/appenders.html) or for more information
see [here](https://logging.apache.org/log4j/2.x/manual/configuration.html#XML).

## Runtime Configuration
The Dataspace Connector allows the modification of logging levels at runtime. To enable this
feature, you will need to locate `application.properties` under `src/main/resources`.

Enable or add the following lines:

```properties
management.endpoints.web.exposure.include=loggers
management.endpoint.loggers.enabled=true
```

A list of all available loggers and their current logging level will be exposed under
`/actuator/loggers`.

To change the logging level at runtime, you will need to perform a `POST` request against the
logger. Here is an example using curl:

```commandline
curl -i -k -X POST -H 'Content-Type: application/json'
    -d '{"configuredLevel": "OFF"}' https://localhost:8080/actuator/loggers/io.dataspaceconnector
```

## Remote Access
To get remote access to the log file, find the `application.properties` at `src/main/resources`.
By default, the Dataspace Connector disables all optional endpoints.

Enable or add the following lines:

```properties
management.endpoints.web.exposure.include=logfile
management.endpoint.logfile.enabled=true
management.endpoint.logfile.external-file=./log/dataspaceconnector.log
```

The logfile will be available by performing a pull request on `/actuator/logfile`.

For more information, see
[here](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html).
