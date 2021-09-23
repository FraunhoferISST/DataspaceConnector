---
layout: default
title: Updates
nav_order: 5
description: ""
permalink: /Deployment/Update
parent: Deployment
---

# Update Information
{: .fs-9 }

Here, you can find instructions on how to access update and further software information.
{: .fs-6 .fw-300 }

---

## Actuator API

With v6.3.0, the Dataspace Connector offers a REST endpoint that checks the currently used version
against the latest available release. If a new version is available, the API will return the
relevant information.

The functionality can be reached by calling the endpoint `GET /actuator/info`.

As a response, you receive a JSON object containing information about the current version as well
as the latest release. This will look as follows, e.g.:

```json
{
  "title": "Dataspace Connector",
  "description": "IDS Connector originally developed by the Fraunhofer ISST",
  "version": "6.2.0",
  "contact": {
    "organization": "Fraunhofer Institute for Software and Systems Engineering",
    "website": "https://www.dataspace-connector.io/",
    "email": "info@dataspace-connector.de"
  },
  "license": {
    "name": "Apache License, Version 2.0",
    "location": "https://www.apache.org/licenses/LICENSE-2.0.txt"
  },
  "camel": {
    "name": "camel-1",
    "version": "3.11.2",
    "startDate": "2021-09-23T12:04:42.682+00:00",
    "uptime": "6s783ms",
    "status": "Started"
  },
  "update": {
    "available": true,
    "version": "6.3.0",
    "type": "Minor",
    "location": "https://github.com/International-Data-Spaces-Association/DataspaceConnector/releases/tag/v6.3.0"
  }
}
```

To make this endpoint accessible, `info` must be present in the allowed web outputs within the
`application.properties`:

```properties
management.endpoints.web.exposure.include=info
```

More information about the actuator API and further configuration options are also available in
[this](logging.md) section and in the Spring Boot
[documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html).
