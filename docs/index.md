---
layout: default
title: Home
nav_order: 1
description: "The Dataspace Connector is an IDS connector reference implementation following the specifications of the IDS Information Model."
permalink: /
---

# Manual and Documentation
{: .fs-9 }

This documentation should help you to become familiar with the Dataspace Connector - to try it out,
integrate it in your use cases, or contribute to its development.
{: .fs-6 .fw-300 }

[Get started now](pages/getting-started.md){: .btn .btn-primary .fs-5 .mb-4 .mb-md-0 .mr-2 } [View it on GitHub](https://github.com/International-Data-Spaces-Association/DataspaceConnector){: .btn .fs-5 .mb-4 .mb-md-0 }

---

The Dataspace Connector is an IDS connector that is being developed at Fraunhofer ISST. With the
help of the Dataspace Connector, existing software can easily be extended by IDS connector
functionalities in order to integrate them into an IDS data ecosystem. Furthermore, it is possible
to use the Dataspace Connector as a basis for the development of own software that is to be
connected to an IDS data ecosystem.

The Dataspace Connector uses the recent IDS Information Model version and the IDS Messaging Services
for message handling with other IDS components. For managing datasets by
means of their metadata as IDS resources, the Dataspace Connector provides a REST API. After an
initial registration, IDS resources are persisted to an internal or external database of the
connector. External data sources can be connected via REST endpoints, allowing the Dataspace
Connector to act as an intermediary between the IDS data ecosystem and the actual data source.

Following the requirements of the International Data Spaces, TLS-encrypted communication with other
IDS connectors and, for example, communication with an IDS broker are supported in the context of an
IDS data ecosystem. The Dataspace Connector can simultaneously act as both a data provider and a
data consumer, and thus both provide data in a data ecosystem and request it from other IDS
connectors. The Dataspace Connector supports various usage control rules, which are implemented and
enforced. This allows data in the IDS data ecosystem to be assigned usage control rules and ensures
data sovereignty throughout the data lifecycle. Furthermore, identity management is supported by the
integration of an identity provider in the IDS context, such as a [DAPS](https://github.com/International-Data-Spaces-Association/IDS-G/tree/master/core/DAPS).

The Dataspace Connector is an open source project whose development is being driven in collaboration
with various research institutes and companies. Its architecture allows the existing implementation
to be adapted as needed for domain-specific requirements. The deployment of the Dataspace Connector
can be run in Docker as well as in Kubernetes.

## IDS-ready

<img width="240" height="271" align="right" src="https://www.isst.fraunhofer.de/de/news/pressemitteilungen/2020/Dataspace-Connector/jcr:content/fixedContent/pressArticleParsys/textwithasset/imageComponent/image.img.4col.png/1608540266652/ids-ready.png">

"The aim of the Dataspace Connector is to provide companies with an easy and trustworthy entry into
the International Data Spaces. There are three levels of certification for the International Data
Spaces, an initiative for cross-industry data exchange with over 100 European companies: Base,
Trusted and Trusted+. The DSC was deliberately tested for the Base certification level, as this does
not require specific hardware such as Trusted Platform Module chips in order to use the connector.
This makes it easier to use the DSC on different hardware and in cloud environments with a
reasonable sacrifice of hardware security features.

In addition, the Dataspace Connector is the only IDS connector that already supports the enforcement
of eight usage condition classes of the International Data Spaces Association and thus exceeds the
Base certification level."

— [News Release](https://www.isst.fraunhofer.de/de/news/pressemitteilungen/2020/Dataspace-Connector.html)
