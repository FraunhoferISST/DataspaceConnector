---
layout: default
title: Communication Guide
nav_order: 6
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
policies, and providing and requesting multiple artifacts at once.
