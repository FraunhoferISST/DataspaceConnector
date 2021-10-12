---
layout: default
title: Clearing House
nav_order: 4
description: ""
permalink: /CommunicationGuide/v6/IdsEcosystem/ClearingHouse
parent: IDS Ecosystem
grand_parent: Communication Guide
---

# IDS Metadata Broker
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
