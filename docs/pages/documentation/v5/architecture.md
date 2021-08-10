---
layout: default
title: Architecture
nav_order: 1
description: ""
permalink: /Documentation/v5/Architecture
parent: Version 5
grand_parent: Documentation
---

# Architecture
{: .fs-9 }

Have a look at the Dataspace Connector's architecture.
{: .fs-6 .fw-300 }

---

The following illustration visualizes the interaction of the Dataspace Connector, the
[IDS Messaging Services](https://github.com/International-Data-Spaces-Association/IDS-Messaging-Services),
the [Configuration Manager](https://github.com/FraunhoferISST/IDS-ConfigurationManager), and it's
[GUI](https://github.com/International-Data-Spaces-Association/IDS-ConfigurationManager-UI).
All components have a defined API that allows individual components to be removed or replaced. The
Dataspace Connector can be deployed standalone and can be connected to existing backend systems.
Configuration Manager and GUI facilitate the operation and configuration of the connector. If
desired, the Dataspace Connector may be replaced by another connector implementation, either
integrating the IDS Messaging Services or not.

For the use of data apps, the data exchange between these and the Dataspace Connector takes place
by using an integration framework, such as Apache Camel, via the defined APIs.
To keep the Dataspace Connector as lightweight and modular as possible, frameworks like Apache Camel
will not be directly integrated into the core container. Instead, they are executed in parallel
and can thus be easily replaced by other frameworks, e.g. Apache Airflow.
The Configuration Manager defines and manages the routes at connector runtime and can thereby control
the data flow between different systems and apps. A monitoring system, e.g. Yacht (next to or inside
the Configuration Manager), helps to monitor and manage the loads of the individual components,
download images, and start or stop pods/containers.

![Connector Setup](../../../assets/images/dsc_architecture.png)

All functionalities and architectural decisions aim at providing a maintainable and easily
extensible software that encapsulates the IDS information model from connected systems.

The basic communication logic is inside the IDS Messaging Services, whereas the business logic is in
focus of the Dataspace Connector itself.

**Is the Dataspace Connector the Execution Core of an IDS Connector?**

Referring to the IDS Reference Architecture of a Connector, it can be stated that the Dataspace
Connector is not a classic Execution Core. It can be extended by the ConfigManager, but can also be
used without it. That is important for many smaller use cases and also the reason why the Connector
is e.g. not divided into control and data plane. On top of that, it does not contain a message
bus/router.

**Why is Camel separate?**

Not every use case needs Data Apps, so the Dataspace Connector should be designed lightweight.
The idea is to enable different message bus systems, like Apache Airflow, Argo, Kafka, etc.

## Network Architecture
The Dataspace Connector will support a segmented network. Every running container will be associated
to a different network zone by providing its own virtual network stack. The Connector as the core
container will have root rights and be able to manage network and firewall configurations for all
separated containers and their networks. As root namespace, it provides an external IP and can be
reached from an external network. Details can be found [here](../../roadmap/concept.md).
