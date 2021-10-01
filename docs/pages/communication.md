---
layout: default
title: Communication Guide
nav_order: 7
description: ""
permalink: /CommunicationGuide
has_children: true
has_toc: true
---

# Communication Guide
{: .fs-9 }

This page explains how to use the APIs of the connector.
{: .fs-6 .fw-300 }

---

You have configured and deployed your Dataspace Connector as described [here](deployment.md)? Then
check out our step-by-step guide to see the Connector in action.

To interact with the running application, the provided
[endpoints](deployment/build.md#maven) can be used - either automated by an application or manually
by interacting with the Swagger UI. In this section, it is explained how to provide local and remote
data with a connector and how to consume this from another one.

---

**Note**: The Dataspace Connector's repository comes with a `scripts/tests` folder that provides
some Python scripts. They contain the creation of a full data offering and its consumption by a
consumer: for a single resource with a single usage policy, a single resource with multiple usage
policies, and providing and requesting multiple artifacts at once. Feel free to use or modify them
when setting up a data exchange example deployment.

---

## Test Deployment

An instance of the Dataspace Connector is currently available in the IDS Lab at
[https://simpleconnector.ids.isst.fraunhofer.de/](https://simpleconnector.ids.isst.fraunhofer.de/).
It can only be reached from inside a VPN network. To get your IP address unblocked, please contact
[us](mailto:info@dataspace-connector.de).
* The connector self-description is available at [https://simpleconnector.ids.isst.fraunhofer.de/](https://simpleconnector.ids.isst.fraunhofer.de/) (GET).
* The open endpoint for IDS communication is
  [https://simpleconnector.ids.isst.fraunhofer.de/api/ids/data](https://simpleconnector.ids.isst.fraunhofer.de/api/ids/data) (POST).
* The backend API and its endpoints (`/api/**`) are only accessible to users with admin rights.

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

The currently implemented support is explained [here](communication/v6/ecosystem.md) in more detail.
