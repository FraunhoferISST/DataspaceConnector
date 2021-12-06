---
layout: default
title: Getting Started
nav_order: 4
description: ""
permalink: /GettingStarted
---

# Getting Started
{: .fs-9 }

Get an example setup running without diving into the code.
{: .fs-6 .fw-300 }

---

## Quick Start

**We provide Docker images. These can be found [here](https://github.com/orgs/International-Data-Spaces-Association/packages/container/package/dataspace-connector).
The GitHub Container Registry (GHCR) allows to download Docker images without credentials.
You will find an image for each release tag with the corresponding version. In addition, an image of
the main branch is automatically provided as soon as changes are identified.**

If you want to build and run locally, ensure that at least Java 17 is installed. Then, follow these
steps:

1.  Clone the repository: `git clone https://github.com/International-Data-Spaces-Association/DataspaceConnector.git`.
2.  Execute `cd DataspaceConnector` and `./mvnw clean package`.
3.  Navigate to `/target` and run `java -jar dataspaceconnector-{VERSION}.jar`.
4.  If everything worked fine, the connector is available at
    [https://localhost:8080/](https://localhost:8080/). The API can be accessed at
    [https://localhost:8080/api](https://localhost:8080/api). The Swagger UI can be found at
    [https://localhost:8080/api/docs](https://localhost:8080/api/docs).

For a more detailed explanation, see [here](deployment.md).


## Test Deployments

The IDSA community provides a free
[GitHub repository](https://github.com/International-Data-Spaces-Association/IDS-Deployment-Examples)
with sample deployments. These include not only the Dataspace Connector or a deployment together
with ConfigManager and GUI, but also some with other IDS components. The goal is to provide an easy
entry into the whole IDS ecosystem. Feel free to have a look at the files or contribute with your
own examples.

Running IDS test instances for the [Dataspace Connector](communication.md#test-deployment) and other
[IDS participants](communication/v6/ecosystem.md) are documented [here](communication.md).
