---
layout: default
title: IDS Ecosystem
nav_order: 3
description: ""
permalink: /CommunicationGuide/v6/IdsEcosystem
parent: Communication Guide
---

# IDS Ecosystem
{: .fs-9 }

Find a concept on how to exchange data via different protocols in this section.
{: .fs-6 .fw-300 }

---

## IDS Metadata Broker

- `POST /api/ids/connector/update`: send a `ConnectorUpdateMessage` with the connector's
  self-description as `payload`
- `POST /api/ids/connector/unavailable`: send a `ConnectorUnavailableMessage` to unregister the connector
- `POST /api/ids/resource/update`: update a previously created resource offer
- `POST /api/ids/resource/unavailable`: remove a previously registered resource offer
- `POST /api/ids/query`: send a `QueryMessage` with a SPARQL command (request parameter) as `payload`

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
