---
layout: default
title: Build
nav_order: 2
description: ""
permalink: /Deployment/Build
parent: Deployment
---

# Build
{: .fs-9 }

Take a look at the more detailed instructions to deploy the Dataspace Connector.
{: .fs-6 .fw-300 }

---

If you want to set up the Dataspace Connector yourself, follow the instructions below. If you
encounter any problems, please have a look at the [FAQ](../faq.md).

Clone the project: `git clone https://github.com/International-Data-Spaces-Association/DataspaceConnector.git`

**For configurations, please have a look at [this section](configuration.md) first.**

In the following, the deployment with Maven, Docker, and Kubernetes will be explained.

## Maven

If you want to build and run locally, ensure that at least Java 11 is installed. Then, follow these
steps:

1.  Execute `cd DataspaceConnector` and `mvn clean package`.
2.  The connector can be started by running the Spring Boot Application. Therefore, navigate to
    `/target` and run `java -jar dataspaceconnector-{VERSION}.jar`.

If everything worked fine, the connector is available at
[https://localhost:8080/](https://localhost:8080/) and its API can be accessed at
[https://localhost:8080/api](https://localhost:8080/api). By default, the Dataspace Connector is
running with an h2 database.

---

**Note**: After successfully building the project, the Javadocs as a static website can be found
at `/target/apidocs`. Open the `index.html` in a browser of your choice.

---

The OpenApi documentation can be viewed at [https://localhost:8080/api/docs](https://localhost:8080/api/docs).
The JSON representation is available at [https://localhost:8080/v3/api-docs](https://localhost:8080/v3/api-docs).
The .yaml file can be downloaded at [https://localhost:8080/v3/api-docs.yaml](https://localhost:8080/v3/api-docs.yaml).

The connector provides several endpoints for resource database handling and IDS messaging. Details
on how to interact with them can be found [here](../communication.md).

*  `Connector` and `Usage Control` provide information about the running connector and settings for
   contract negotiation and policy enforcement behaviour.
*  `IDS Messages` provides endpoints for requesting artifact (data) and descriptions (metadata) from
   an external connector, and negotiate contracts. On top of that, endpoints for sending IDS
   multipart messages to e.g. the IDS Metadata Broker are provided.
*  All other sections provide endpoints for metadata and data management and entity relations
   (`POST`, `PUT`, `GET`, and `DELETE`).

Next to the endpoint implemented by the connector, an endpoint for handling incoming IDS messages
at `/api/ids/data` is provided by the IDS Messaging Services.

The database can be accessed via [https://localhost:8080/database](https://localhost:8080/database).

### Profiles
The `pom.xml` provides three Maven profiles: `no-documentation`, `no-tests`, and
`release`. The first one skips the Javadocs generation, the second one skips the execution of
tests. The `release` profile shows all warnings and errors. To run a profile, please have a look at
[this guide](maven.apache.org/guides/introduction/introduction-to-profiles.html#details-on-profile-activation).

### Plugins

| Plugin | Command | Description |
|:-------|:--------|:------------|
| Checkstyle | mvn checkstyle:check | With this, a codestyle check is executed. |
| Statistics | mvn verify site | With this, project statistics are generated. |
| License | mvn license:format | With this, a license header is added to all projects files that are missing one. |

## Docker

If you want to deploy in Docker and build the Maven project with the `Dockerfile`, follow these
steps:

### Option 1: Build and run Docker image
1. Navigate to `DataspaceConnector`. To build the image, run `docker build -t <IMAGE_NAME:TAG> .`
   (e.g. `docker build -t dataspaceconnector .`).
2. For running your image as a container, follow [these](https://docs.docker.com/get-started/part2/)
   instructions: `docker run --publish 8080:8080 --detach --name dsc-container <IMAGE_NAME:TAG>`

### Option 2: Using Docker Compose
1. The `docker-compose.yml` sets up the connector application and a PostgreSQL database. If
   necessary, make your changes in the `connector.env` and `postgres.env`. Please find more details
   about setting up different databases [here](database.md).
2. For starting the application, run `docker-compose up`. Have a look at the `docker-compose.yaml`
   and make your own configurations if necessary.

---

**Note**: Environment variables will overwrite the Spring Boot settings.

---

## Kubernetes

For a deployment with Kubernetes, have a look at [this](kubernetes.md) documentation.

## Tests

Tests will be executed automatically when running Maven commands `package`, `verify`, `install`,
`site`, or `deploy`. To run tests manually, execute the following commands in the root directory of
the project:
* Run all tests: `mvn test`
* Run specific test class: `mvn test -Dtest=[full class name]`
* Run a specific test case (single method): `mvn test -Dtest=[full class name]#[method name]`
