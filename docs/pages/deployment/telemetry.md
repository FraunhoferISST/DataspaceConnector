---
layout: default
title: Telemetry
nav_order: 7
description: ""
permalink: /Deployment/Telemetry
parent: Deployment
---

# Telemetry
{: .fs-9 }

You want to have insights into a running Dataspace Connector? See what you have to do here.
{: .fs-6 .fw-300 }

---

To enable the telemetry collection via Jaeger, set `opentracing.jaeger.enabled=true` in the
`application.properties`.

The Dataspace Connector will now send telemetry data via UDP to `localhost:6831`. The name of the
Dataspace Connector displayed in the logs is defined in `spring.application.name`.

To change the target of the UDP packages, modify `opentracing.jaeger.udp-sender.host` and
`opentracing.jaeger.udp-sender.port` respectively. For further modification options and settings
have a look [here](https://github.com/opentracing-contrib/java-spring-jaeger).
