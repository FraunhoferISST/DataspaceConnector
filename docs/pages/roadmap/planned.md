---
layout: default
title: Planned Features
nav_order: 1
description: ""
permalink: /Features/PlannedFeatures
parent: Roadmap
---

# Planned Features
{: .fs-9 }

For transparent and collaborative development, the project's roadmap and each aspect are described in detail.
{: .fs-6 .fw-300 }

---

Some requirements are derived from the IDS-ready implementation concept as listed
[here](concept.md). Below, you can find an overview of general requirements that will be
addressed in future development. We distinguish between core and ids functionality.

## Core-Functionality

This list follows no timeline. Instead, individual tasks can be priority-assigned. Thus, the
implementation can be freely scheduled and extended, depending on the interests and projects of
potential contributors. The rating ranges from 1 (= high priority) to 3 (= low priority).

| Priority | Task                                  | Status      | Note        |
|:---------|:--------------------------------------|:------------|:------------|
| 2        | Connector Orchestration in Kubernetes | in progress |    |
| 2        | Network Support                       |             |    |
| 2        | Identity & Access Management          |             |    |
| 2        | Software Tests                        | ongoing     |    |
| 1        | Software Documentation                | ongoing     |    |
| 3        | Connection to Hyperscalers as e.g. AWS|             |    |
| 1        | Audit Logging                         |             |    |
| 1        | Code Quality Improvements             | done        |    |
| 1        | Database Improvement                  | done        |    |
| 1        | Exception Handling                    | done        |    |
| 1        | Logging                               | done        |    |
| 3        | Scalability                           |             |    |
| 2        | Data Streaming                        | [concept](../communication/v6/streaming.md) | |
| 2        | Asynchronous Message Handling         | out-of-scope|    |

## IDS-Functionality

The implementation of the following requirements primarily focuses on IDS developments.
Responsible for this are mainly developers from the Fraunhofer ISST.

| Timeline | Task                                   | Status      | Note        |
|:---------|:---------------------------------------|:------------|:------------|
| Q4/20    | Ids-ready Test                         | done        |    |
|          | ConfigManager Integration              | done        |    |
|          | Basic Policy Negotiation               | done        |    |
| Q1/21    | Routing (e.g. Apache Camel)            | in progress | integration via ConfigManager |
|          | App (Store) Integration                | in progress | integration via ConfigManager |
|          | Support Query Parameters               | done        | [concept](../communication/v6/parametrization.md)   |
| Q2/21    | Usage Control Extension                | done        |    |
|          | Support ParIS                          | -           | postponed |
|          | Support Vocabulary Provider            | -           | postponed |
|          | Extended Clearing House Integration    | done        |    |
|          | Support of complex REST backends       | done        |    |
|          | Data Push                              | done        | [concept](../communication/v6/streaming.md) |
|          | Publish/Subscribe                      | done        | [concept](../communication/v6/subscription.md) |
|          | Query Broker                           | done        | postponed to Q2 |
|          | Integration of IDSCP 2.0               | done        | postponed to Q2 |
|          | IDS-LDP Integration                    | -           | postponed |
|          | Basic Clearing House Integration       | done        | postponed to Q2 |

## Details

Please find some detailed explanation of the aforementioned roadmap items below.

* Ids-ready Test: The test for the IDS-ready label comprises different requirements, which are
  assigned to the 3 levels base, trust and trust+. The last IDSA Plugfest has shown that these 3
  possible levels will be extended. With the DSC, we are aiming for the base profile. Currently, we
  are writing a concept for the IDS-ready label test. This includes exactly the criteria that will
  also be checked during the later certification. At that time, however, the connector has to
  actually implement the requirements.
* Routing (e.g. Apache Camel): As described [here](../documentation/v5/architecture.md), the
  integration of a routing framework as e.g. Apache Camel will improve and extend the data flow and
  its interception.
* Usage Control Extension: Currently, the Dataspace Connector supports ten policy patterns out
  of 21. Policy enforcement should be configurable. For this, the current access and usage control
  must be modular and interchangeable. As a possible alternative, MyData and a corresponding
  interceptor for the data flow will be integrated into the DSC. On top of that, the Connector
  should provide a possibility to define custom policy patterns.
* Support ParIS: In order to provide additional information about the IDS participants, the DSC
  needs the connection of the Participant Information Service (ParIS) provided by the Fraunhofer
  IAIS.
* Support Vocabulary Provider: For the integration of user-specific vocabularies for data
  description, the Vocabulary Provider VoCol will be integrated.
