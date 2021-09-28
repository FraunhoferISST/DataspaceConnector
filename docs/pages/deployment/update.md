---
layout: default
title: Update
nav_order: 5
description: ""
permalink: /Deployment/Update
parent: Deployment
---

# Dataspace Connector version updates.
{: .fs-9 }

Here, you can find instructions on how to find out if there is a more recent version of the Dataspace Connector released.
{: .fs-6 .fw-300 }

---

## Actuator-API

The Dataspace Connector has a REST API that checks the currently used version against the latest available release. If a new version is available, the API will return the relevant information.

The functionality can be reached by calling the API:

```xml
/actuator/info
```

The API supports JSON and outputs information about the current version as well as the last release.

If there is an update, the API will output the following, for example:

```xml
connector:
   connector.version:   "6.0.0"
connector.update:
   update.type:         "minor update"
   update.available:    true
   update:version:      "6.2.0"
   update:location:     "https://github.com/International-Data-Spaces-Association/DataspaceConnector/releases/tag/v6.2.0"
```

To make this API usable, `info` must be present in the allowed web outputs within the `application.properties`.

```xml
management.endpoints.web.exposure.include=info
```

More information about the actuator API and further configuration options are also available in the logging documentation section.
