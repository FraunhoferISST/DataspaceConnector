---
layout: default
title: Telemetry
nav_order: 3
description: ""
permalink: /Deployment/Configuration/Telemetry
parent: Configuration
grand_parent: Deployment
---

# Telemetry
{: .fs-9 }

You want to have insights into a running Dataspace Connector? See what you have to do here.
{: .fs-6 .fw-300 }

---

To enable the telemetry collection via Jaeger, modify the corresponding value in the
`application.properties`:
```properties
opentracing.jaeger.enabled=true
```

The Dataspace Connector will now send telemetry data via UDP to `localhost:6831`. The name of the
Dataspace Connector displayed in the logs is defined in `spring.application.name`.

To change the target of the UDP packages, modify `opentracing.jaeger.udp-sender.host` and
`opentracing.jaeger.udp-sender.port` respectively. For further modification options and settings
have a look [here](https://github.com/opentracing-contrib/java-spring-jaeger).

To view tracing information, a Docker container has to be started:
```
docker run -d --name jaeger \
  -e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 14250:14250 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.22
```
The traces can then be accessed at [http://localhost:16686](http://localhost:16686).
