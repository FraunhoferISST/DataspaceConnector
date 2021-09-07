---
layout: default
title: Features
nav_order: 3
description: ""
permalink: /Features
---

# Features
{: .fs-9 }

Find here an overview of functionalities, used IDS libraries, and integrated IDS components.
{: .fs-6 .fw-300 }

---

The Dataspace Connector uses modern technologies, standards (e.g. RFC 7231, IDS Information Model,
IDS Usage Control Language), and best practices (pattern implementation, e.g. MVC).
Software quality is ensured by adhering to and implementing code style guides and logging and
providing high test coverage. Quality checks and project reports can be generated via maven plugin.

`Java` `Maven` `Spring Boot` `Spring Data JPA` `Spring Security` `OpenAPI` `HATEOAS` `Swagger`
`LOG4J2` `Docker` `Kubernetes` `JSON(-LD)` `Jaeger` `TLS`

All functionalities and architectural decisions aim at providing a maintainable and easily
extensible software that encapsulates the IDS information model from connected systems.

* Identity management: Central Identity Provider/[DAPS](https://github.com/International-Data-Spaces-Association/IDS-G/tree/master/core/DAPS), IDS certificates (X.509v3)
* API for (meta) data management and IDS communication
  * Partially support of HATEOAS
  * Management of metadata (optionally also data) in local database (e.g. PostgreSQL)
  * Connection of remote data sources (possibility of queries on data sets)
* Clear interfaces between data model and the IDS Infomodel
  * Strict implementation of MVC pattern for data management
  * Strict access control to backend, information can only be read and changed by services
  * Strict state validation for entities via factory classes
  * Storage of remote IDs and addresses to objects for origin tracking
* Communication via IDS protocols
  * Interaction with other IDS participants as data provider & consumer
  * TLS encrypted communication via IDS Multipart Messages
  * Camel-base communication via IDSCPv2
  * Automated messaging sequence
  * IDS Metadata Broker: un/register connector, un/register resources, query offers
  * Clearing House: log contract agreements, data usage, artifact requests, and artifact responses
* IDS Usage Control Language: ten supported Usage Control Patterns and policy negotiation
* Subscription transfer pattern
  * Un-/Subscribe to requests, representations, and artifacts as a non-IDS system/app
  * Un-/Subscribe to offers, representations, and artifacts as an IDS connector via IDS messages
  * Manually and automatically sending Resource Update Messages for receiving latest metadata and
    data changes
* Integration and configuration of Jaeger for using open telemetry
* Optional http tracing for transparent information and data flow
* Optional bootstrapping for registering resource offers on start-up
* Apps and Routes
  * Manage routes via REST API
  * Deploy Camel routes at runtime
* Security
  * Prevent leaking of technology stack in case of errors/exceptions
  * Logger sanitizes inputs to prevent CRLF injections
  * Common CVE patches


## Libraries

| Library | License | Owner | Contact |
|:--------|:--------|:------|:--------|
| [IDS Information Model Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel/) | [Apache 2.0](https://github.com/International-Data-Spaces-Association/Java-Representation-of-IDS-Information-Model) | Fraunhofer IAIS | [E-Mail IAIS](mailto:contact@ids.fraunhofer.de) |
| [IDS Information Model Serializer Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel-serializer/) | Apache 2.0 | Fraunhofer IAIS | [E-Mail IAIS](mailto:contact@ids.fraunhofer.de) |
| [IDS Messaging Service](https://github.com/International-Data-Spaces-Association/IDS-Messaging-Services) | [Apache 2.0](https://github.com/International-Data-Spaces-Association/IDS-Messaging-Services) | Fraunhofer ISST & IAIS | [E-Mail ISST](mailto:info@dataspace-connector.de), [E-Mail IAIS](mailto:contact@ids.fraunhofer.de) |

The [ConfigManager](https://github.com/FraunhoferISST/IDS-ConfigurationManager) and its
[GUI](https://github.com/International-Data-Spaces-Association/IDS-ConfigurationManager-UI) are a
part of the IDS Connector and aim to facilitate the configuration of the Dataspace Connector and
further IDS Connector implementations. Both projects are also open source and licensed under
Apache 2.0.

**Update**: The IDS Configuration Manager has been directly integrated into the Dataspace Connector
core and thus will be maintained in the Dataspace Connector repository. The GUI interacts with all
given interfaces to provide all functionality that can also be directly triggered at the
Connector's REST API.


## IDS Communication

| Component | License | Owner | Contact |
|:--------|:--------|:------|:--------|
| [IDS Broker](https://broker.ids.isst.fraunhofer.de/) | [Apache 2.0](https://github.com/International-Data-Spaces-Association/metadata-broker-open-core) | Fraunhofer IAIS | [E-Mail](mailto:contact@ids.fraunhofer.de) |
| [DAPS](https://daps.aisec.fraunhofer.de/) | [Apache 2.0](https://github.com/Fraunhofer-AISEC/omejdn-server) | Fraunhofer AISEC | [Gerd Brost](mailto:gerd.brost@aisec.fraunhofer.de) |
| [ParIS](https://paris.ids.isst.fraunhofer.de/) | [Apache 2.0](https://github.com/International-Data-Spaces-Association/ParIS-open-core) | Fraunhofer IAIS | [E-Mail](mailto:contact@ids.fraunhofer.de)
