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
The GitHub Container Registry (GHCR) allows downloading Docker images without credentials.
You will find an image for each release tag with the corresponding version. In addition, an image of
the main branch is automatically provided as soon as changes are identified.**

### Docker

For an easy deployment, make sure that you have [Docker](https://docs.docker.com/get-docker/)
installed. Then, execute the following command:

```commandline
docker run -p 8080:8080 --name connector ghcr.io/international-data-spaces-association/dataspace-connector:latest
```

### Local Build

If you want to build and run locally, ensure that at least Java 17 is installed. Then, follow these
steps:

1. Clone the repository: `git clone https://github.com/International-Data-Spaces-Association/DataspaceConnector.git`.
2. After that, switch to the downloaded directory with `cd DataspaceConnector`.
3. We highly recommend to only use tagged commits. The `main` branch may also contain unstable
   changes and features. Therefore:
   1. List all available tags with: `git tag -l`. (optional)
   2. Choose the latest tag and execute: `git checkout tags/<tag_name>`, e.g. `git checkout tags/v7.0.0`.
4. To build the project, run `./mvnw clean package`.
5. Navigate to `/target` and run `java -jar dataspaceconnector-{VERSION}.jar`.

### Interacting with the running Connector

If everything worked fine, the connector is available at [https://localhost:8080/](https://localhost:8080/).
The API can be accessed at [https://localhost:8080/api](https://localhost:8080/api).
The Swagger UI can be found at [https://localhost:8080/api/docs](https://localhost:8080/api/docs).

For certain REST endpoints, you will be asked to log in. The default credentials are `admin` and
`password`. **Please take care to change these when deploying and hosting the connector yourself!**

For a more detailed explanation of deployment and configurations, see [here](deployment.md).

Next, please take a look at our
[communication guide](https://international-data-spaces-association.github.io/DataspaceConnector/CommunicationGuide).

## Test Deployments

The IDSA community provides a free
[GitHub repository](https://github.com/International-Data-Spaces-Association/IDS-Deployment-Examples)
with sample deployments. These include not only the Dataspace Connector or a deployment together
with ConfigManager and GUI, but also some with other IDS components. The goal is to provide an easy
entry into the whole IDS ecosystem. Feel free to have a look at the files or contribute with your
own examples.

Running IDS test instances for the [Dataspace Connector](communication.md#test-deployment) and other
[IDS participants](communication/v6/ecosystem.md) are documented [here](communication.md).
