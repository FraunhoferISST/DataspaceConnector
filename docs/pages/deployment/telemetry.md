---
layout: default
title: Telemetry
nav_order: 7
description: ""
permalink: /Deployment/Telemetry
parent: Deployment
---

# Telemetry

To enable the telemetry collection via jaeger find the `application.properties` and
set `opentracing.jaeger.enabled=true`.
The Dataspace Connector will now send telemetry data via UDP to `localhost:6831`. The name of the
Dataspace Connector displayed in the logs is defined in `spring.application.name`.

To change the target of the UDP packages modify `opentracing.jaeger.udp-sender.host` and
`opentracing.jaeger.udp-sender.port` respectively.

For further modification options and settings consult [here](https://github.com/opentracing-contrib/java-spring-jaeger).
