---
layout: default
title: Clearing House
nav_order: 4
description: ""
permalink: /CommunicationGuide/v6/IdsEcosystem/ClearingHouse
parent: IDS Ecosystem
grand_parent: Communication Guide
---

# IDS Clearing House
{: .fs-9 }

This section provides a detailed guide on communication with the IDS Clearing House.
{: .fs-6 .fw-300 }

---

The Clearing House is available at [https://ch-ids.aisec.fraunhofer.de/](https://ch-ids.aisec.fraunhofer.de/).
The repositories for [core](https://github.com/International-Data-Spaces-Association/ids-clearing-house-core)
and [service](https://github.com/International-Data-Spaces-Association/ids-clearing-house-service)
are open source and can be accessed at GitHub. Further documentation about the IDS Clearing House
can be seen [here](https://github.com/International-Data-Spaces-Association/IDS-G/blob/main/Components/ClearingHouse/README.md).

The Dataspace Connector has local logging, that can be changed following
[these](../../../deployment/logging.md) steps. In addition, it logs some information to the Clearing
House:
* finalized contract agreements,
* data usage (if noted in a usage policy),
* incoming and outgoing `ArtifactRequestMessages`,
* and incoming and outgoing `ArtifactResponseMessages`.

### Querying the Clearing House

Information logged to the Clearing House can be queried and viewed using the `/api/ids/query`
endpoint. In order to get any logged information, the `process ID` under which it was logged
has to be supplied. The DSC always uses the UUID of the contract agreement for logging the
agreement itself as well as any data request, data response and data usage made under that
agreement. As both provider and consumer log under the same process ID, the agreement's UUID on
provider side is used. On consumer side, this can be found in the URI in the field
`remoteId` of the agreement.

To query all information logged under an agreement UUID, call `/api/ids/query` with the following input:
* `recipient` parameter: `<clearing-house-url>/messages/query/<agreement-uuid>`
* request body: `""`

**Note, that only the connectors, between which the contract agreement has been made, can view the
information logged under that agreement.**

