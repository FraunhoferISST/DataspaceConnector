---
layout: default
title: Identity Provider
nav_order: 1
description: ""
permalink: /CommunicationGuide/v6/IdsEcosystem/IdentityProvider
parent: IDS Ecosystem
grand_parent: Communication Guide
---

# IDS Participant Information System
{: .fs-9 }

This section provides a detailed guide on communication with the IDS Identity Provider.
{: .fs-6 .fw-300 }

---

## Dynamic Attribute Provisioning Service (DAPS)

IDS connectors request a digitally signed JSON web token (JWT) from a central IDS component called
Dynamic Attribute Provisioning Service (DAPS) in order to authenticate themselves. Without these
DAPS tokens (DATs) no connector can participate in the IDS.

The Dataspace Connector communicates with the DAPS provided by the Fraunhofer AISEC by default. It
is available at [https://daps.aisec.fraunhofer.de/](https://daps.aisec.fraunhofer.de/).

The [repository](https://github.com/International-Data-Spaces-Association/omejdn-daps) is open
source and can be accessed at GitHub. Further documentation about the IDS Identity Provider/DAPS can
be seen [here](https://github.com/International-Data-Spaces-Association/IDS-G/blob/main/Components/IdentityProvider/README.md).
The content of a Dynamic Attribute Token (DAT) is listed and explained
[here](https://github.com/International-Data-Spaces-Association/IDS-G/blob/main/Components/IdentityProvider/DAPS/README.md#dynamic-attribute-token-content).

### AISEC DAPS: Issuing an IDS certificate

If you want to locally test a certain setup of IDS connectors, there is some sample key material
available for localized docker domains such as (provider-core or consumer-core) which can be
downloaded [here](https://github.com/industrial-data-space/trusted-connector/tree/master/examples/etc),
or as complete sample set[here](https://github.com/industrial-data-space/trusted-connector/blob/master/examples/trusted-connector-examples_latest.zip).

If you want to run your IDS-connector on an internet domain, such as connector.aisec.fraunhofer.de,
for instance, there are two steps involved to receive your key material to authenticate at the IDS
DAPS:

1. Register for an account at the IDS Association's
   [website](https://internationaldataspaces.org/we/get-access-to-jive/) and wait for manual
   approval for 1-2 days.

2. Enter a request for a DAPS certificate in
   [this](https://industrialdataspace.jiveon.com/docs/DOC-2002) list. The mandatory pieces of
   information for the certificate include:
   - Country,
   - Organization,
   - Organizational Unit,
   - and Domain.

Afterwards, you will receive the certificate and its corresponding key material bundled as
.p12-archive via e-mail.

In urgent cases, you can also directly contact us for a certificate request, but we highly encourage
using the standard way. Our mailing list dealing with DAPS certificates and questions regarding the
key material (not the setup of other connectors) is
[daps-certificates@aisec.fraunhofer.de](mailto:daps-certificates@aisec.fraunhofer.de).

## Participant Information System (ParIS)

The Participant Information System (ParIS) is available at [https://paris.ids.isst.fraunhofer.de](https://paris.ids.isst.fraunhofer.de).
It expects IDS multipart messages at [https://paris.ids.isst.fraunhofer.de/infrastructure](https://paris.ids.isst.fraunhofer.de/infrastructure).
The GUI can be accessed at [https://paris.ids.isst.fraunhofer.de/browse](https://paris.ids.isst.fraunhofer.de/browse).
To get your IP address unblocked, please contact [us](mailto:info@dataspace-connector.de).

The [repository](https://github.com/International-Data-Spaces-Association/ParIS-open-core) is open
core and can be accessed at GitHub. Further documentation about the ParIS can be seen
[here](https://github.com/International-Data-Spaces-Association/IDS-G/blob/main/Components/IdentityProvider/ParIS/README.md).

The Dataspace Connector currently does not offer any ParIS interaction.
