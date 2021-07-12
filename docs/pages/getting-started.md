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

If you want to build and run locally, ensure that at least Java 11 is installed. Then, follow these
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


## Test Instances

An instance of the Dataspace Connector is currently available in the IDS Lab at
[https://simpleconnector.ids.isst.fraunhofer.de/](https://simpleconnector.ids.isst.fraunhofer.de/).
It can only be reached from inside a VPN network. To get your IP address unblocked, please contact
[us](mailto:info@dataspace-connector.de).
* The connector self-description is available at [https://simpleconnector.ids.isst.fraunhofer.de/](https://simpleconnector.ids.isst.fraunhofer.de/) (GET).
* The open endpoint for IDS communication is
  [https://simpleconnector.ids.isst.fraunhofer.de/api/ids/data](https://simpleconnector.ids.isst.fraunhofer.de/api/ids/data) (POST).
* The backend API and its endpoints (`/api/**`) are only accessible to users with admin rights.

### Connector Communication
When requesting the connector's self-description, the included catalog gives information about
available resources. The resource id is essential for requesting an artifact or description.

The open endpoint at `/api/ids/data` expects an `ArtifactRequestMessage` with a known artifact id
as `RequestedArtifact` (for requesting data) or a `DescriptionRequestMessage` with a known
element id as `RequestedElement` (for requesting metadata).
* If this parameter is not known to the connector, you will receive a `RejectionMessage` as
  response.
* If the `RequestedElement` is missing at a `DescriptionRequestMessage`, you will receive the
  connector's self-description.

Possible rejection messages:
* `RejectionMessage` with `RejectionReason.VERSION_NOT_SUPPORTED` if you are not using
  Infomodel v4.x.x.
* `RejectionMessage` with `RejectionReason.NOT_AUTHENTICATED` if the requesting connector has no
  valid DAT.
* `RejectionMessage` with `RejectionReason.BAD_PARAMETERS` if the request contains missing/wrong
  parameters.
* `RejectionMessage` with `RejectionReason.INTERNAL_RECIPIENT_ERROR` if message processing failed.
* `RejectionMessage` with `RejectionReason.NOT_FOUND` if the requested element/artifact could not
  be found.
* `RejectionMessage` with `RejectionReason.NOT_AUTHORIZED` if a policy restriction was detected.
* `ContractRejectionMessage` with `RejectionReason.BAD_PARAMETERS` if the contract request was not
  accepted.

### More IDS

Other IDS components also have running instances that can be used for testing. The Dataspace
Connector currently mainly supports communication with the IDS Broker and [DAPS](https://github.com/International-Data-Spaces-Association/IDS-G/tree/master/core/DAPS) - as described
[here](features.md#ids-communication). A working communication with other components is not
guaranteed.

* [The Dynamic Attribute Provisioning Service](https://github.com/International-Data-Spaces-Association/IDS-G/tree/master/core/DAPS) (DAPS) is available at
[https://daps.aisec.fraunhofer.de/](https://daps.aisec.fraunhofer.de/).

* The IDS Metadata Broker is available at
  [https://broker.ids.isst.fraunhofer.de](https://broker.ids.isst.fraunhofer.de). It expects IDS
  multipart messages at[https://broker.ids.isst.fraunhofer.de/infrastructure](https://broker.ids.isst.fraunhofer.de/infrastructure).
  The GUI can be accessed at [https://broker.ids.isst.fraunhofer.de/browse](https://broker.ids.isst.fraunhofer.de/browse).
  To get your IP address unblocked, please contact [us](mailto:info@dataspace-connector.de).

* The Participant Information System (ParIS) is available at
  [https://paris.ids.isst.fraunhofer.de](https://paris.ids.isst.fraunhofer.de).
  It expects IDS multipart messages at
  [https://paris.ids.isst.fraunhofer.de/infrastructure](https://paris.ids.isst.fraunhofer.de/infrastructure).
  The GUI can be accessed at [https://paris.ids.isst.fraunhofer.de/browse](https://paris.ids.isst.fraunhofer.de/browse).
  To get your IP address unblocked, please contact [us](mailto:info@dataspace-connector.de).
