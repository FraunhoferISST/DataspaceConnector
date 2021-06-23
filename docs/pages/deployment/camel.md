---
layout: default
title: Routing
nav_order: 7
description: ""
permalink: /Deployment/Routing
parent: Deployment
---

# Routing Frameworks
{: .fs-9 }

Here, you can find instructions for using Camel with the Dataspace Connector.
{: .fs-6 .fw-300 }

---

The communication between the Dataspace Connector and data apps can be achieved by using an 
integration Framework like [Apache Camel](https://camel.apache.org/). This also provides the 
possibility to use all kinds of different backends for resources registered in the Connector, as no 
separate implementation has to be made for each possible protocol. To keep the Dataspace Connector 
lightweight and modular, no integration framework will be integrated directly, but rather be 
executed standalone in parallel to the Connector's core container.

The repository [DSC Camel Instance](https://github.com/International-Data-Spaces-Association/DSC-Camel-Instance) 
describes how to use Apache Camel together with the Dataspace Connector and gives examples for 
connecting different backend types.
