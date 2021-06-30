---
layout: default
title: IDS-ready
nav_order: 2
description: ""
permalink: /Roadmap/Ids-ready
parent: Roadmap
---

# IDS-ready Implementation Concept
{: .fs-9 }

Following the IDS certification criteria, an IDS Connector has to provide certain software criteria.
{: .fs-6 .fw-300 }

---

The Dataspace Connector is "IDS-ready" approved since 03.12.2020, for the certification level base
(+ usage control). In the table below is an overview of all criteria for the base profile and
whether it is already implemented or not.

| DSC | No.         | Title       |
|:---:|:------------|:------------|
| x | COM 01      | Protected connection |
| x | COM 02      | Mutual authentication |
| x | COM 03      | State of the art cryptography |
| x | USC 01      | Definition of usage policies |
| x | _USC 02_    | _Sending of usage policies_ |
| x | _USC 03_    | _Usage policy enforcement_ |
| x | INF 01      | Self-Description (at Connector) |
| x | INF 02      | Self-Description (at Broker) |
| x | INF 03      | Self-Description content |
| x | INF 04      | Self-Description evaluation |
| x | INF 05      | Dynamic attribute tokens |
| x | IAM 01      | Connector identifier |
| x | IAM 02      | Time Service |
| x | IAM 03      | Online certificate status check |
| x | IAM 04      | Attestation of dynamic attributes |
| - | BRK 01      | Broker service inquiries |
| x | BRK 02      | Broker registration |
| x | BRK 03      | Broker registration update |
| - | OS 01       | Container support |
| - | APS 01      | App signature |
| - | APS 02      | App signature verification |
| - | APS 05      | App installation |
| - | APS 06      | App Store |
| - | AUD 01      | Access control logging |
| - | AUD 02      | Data access logging |
| - | AUD 03      | Configuration changes logging |
| x | CR 1.1      | Human user identification and authentication |
| - | CR 1.1 (1)  | Unique identification and authentication |
| x | CR 1.2      | Software process and device identification and authentication |
| x | CR 1.2 (1)  | Unique identification and authentication |
| - | CR 1.3      | Account management |
| - | CR 1.4      | Identifier management |
| - | CR 1.5      | Authenticator management |
| - | CR 1.7      | Strength of password-based authentication |
| - | CR 1.8      | Public key infrastructure certificates |
| - | CR 1.9      | Strength of public key-based authentication |
| x | CR 1.10     | Authenticator feedback |
| - | CR 1.11     | Unsuccessful login attempts |
| - | CR 1.12     | System use notification |
| - | CR 1.14     | Strength of symmetric key-based authentication |
| x | CR 2.1      | Authorization enforcement |
| - | CR 2.2      | Wireless use control |
| - | CR 2.5      | Session lock |
| - | CR 2.8      | Auditable events |
| - | CR 2.9      | Audit storage capacity |
| - | CR 2.10     | Response to audit processing failures |
| x | CR 2.11     | Timestamps |
| - | CR 2.12     | Non-repudiation |
| x | CR 3.1      | Communication integrity |
| x | CR 3.1 (1)  | Communication authentication |
| - | CR 3.3      | Security functionality verification |
| - | CR 3.4      | Software and information integrity |
| - | CR 3.5      | Input validation |
| - | CR 3.6      | Deterministic output |
| x | CR 3.7      | Error handling |
| - | CR 3.8      | Session integrity |
| x | CR 4.1      | Information confidentiality |
| - | CR 4.2 (1)  | Erase of shared memory resources |
| x | CR 4.3      | Use of cryptography |
| - | CR 5.1      | Network segmentation |
| - | CR 6.1      | Audit log accessibility |
| - | CR 7.1      | Denial of service protection |
| - | CR 7.2      | Resource management |
| - | CR 7.3      | Control system backup |
| - | CR 7.4      | Control system recovery and reconstitution |
| - | CR 7.6      | Network and security configuration settings |
| x | CR 7.7      | Least functionality |
| - | SAR 2.4     | Mobile code |
| - | SAR 2.4 (1) | Mobile code integrity check |
| - | SAR 2.4 (1) | Protection from malicious code |
| - | NDR 1.6     | Wireless Access Management |
| - | NDR 1.13    | Access via untrusted networks |
| - | NDR 2.4     | Mobile code |
| - | NDR 3.2     | Protection from malicious code |
| - | NDR 3.10    | Support for updates |
| - | NDR 3.14    | Integrity of the boot process |
| - | NDR 5.2     | Zone boundary protection |
| - | NDR 5.3     | General purpose, person-to-person communication restrictions |
| - | D_AD.1      | Secure initialisation |
| - | D_AD.2      | Tamper protection |
| - | D_AD.3      | Security-enforcing mechanisms |
| x | D_IS.1      | Interface purpose and usage |
| x | D_IS.2      | Interface parameters |
| x | D_DD.1      | Subsystem structure |
| - | G_AP.1      | Acceptance procedures |
| x | G_AP.2      | Installation procedures |
| x | G_OG.1      | Interface usage for each user role |
| - | G_OG.2      | Possible modes of operation |
| x | S_CM.1      | Unique component reference |
| x | S_CM.2      | Consistent usage of component reference |
| - | S_CM.6 (1)  | Configuration list content (1) |
| - | S_CM.7      | Unique identification based on configuration list |
| - | S_CM.8      | Developer Information |
| - | S_DL.1      | Secure delivery |
| - | S_FR.1      | Tracking of reported security flaws |
| - | S_FR.2      | Security flaw description |
| - | S_FR.3      | Status of corrective measures |
| x | T_CA.1      | Test coverage analysis |
| - | T_CA.2      | Test procedures for subsystems |
| - | T_TD.1      | Test documentation |
| - | T_TD.2      | Test configuration |
| - | T_TD.3      | Ordering Dependencies |
