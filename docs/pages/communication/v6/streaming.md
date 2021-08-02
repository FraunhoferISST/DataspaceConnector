---
layout: default
title: Streaming
nav_order: 6
description: ""
permalink: /CommunicationGuide/v6/Streaming
parent: Communication Guide
---

# Streaming
{: .fs-9 }

Find a concept on how to exchange data via different protocols in this section.
{: .fs-6 .fw-300 }

---

As the IDS multipart messages exchanged with other IDS Connectors do not support streaming use
cases, we try to define a concept in the following.

Usually, it is intended to exchange data within an `ArtifactResponseMessage` as the reply to an
`ArtifactRequestMessage`. This way, the IDS identity handshake and IDS-specific message details
ensure that a sovereign data exchange happens between valid IDS participants of a data ecosystem.
To being able to support data streaming but not losing the IDS features, we want to send as many
messages as possible and necessary over an IDS protocol and only switch protocols for the actual
data exchange.

That means that, as visualized in the diagram below, the data offer querying, the negotiation phase,
and some information exchange are still send over IDS. However, instead of the actual data, the
`ArtifactResponseMessage` contains some information about where and how the consumer can retrieve
the data from. That may cover e.g. a location, some authentication information, or protocol details.

[![Sequence Diagram Data Exchange](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgcGFydGljaXBhbnQgQ29uc3VtZXJCYWNrZW5kXG4gICAgcGFydGljaXBhbnQgQ29uc3VtZXJDb25uZWN0b3JcbiAgICBwYXJ0aWNpcGFudCBQcm92aWRlckNvbm5lY3RvclxuICAgIHBhcnRpY2lwYW50IFByb3ZpZGVyQmFja2VuZFxuICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-UHJvdmlkZXJDb25uZWN0b3I6IHF1ZXJ5IGRhdGEgb2ZmZXJzXG4gICAgUHJvdmlkZXJDb25uZWN0b3ItLT4-Q29uc3VtZXJDb25uZWN0b3I6IHJldHVybiBtZXRhZGF0YVxuICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-UHJvdmlkZXJDb25uZWN0b3I6IGNvbnRyYWN0IHJlcXVlc3RcbiAgICBQcm92aWRlckNvbm5lY3Rvci0tPj5Db25zdW1lckNvbm5lY3RvcjogY29udHJhY3QgYWdyZWVtZW50XG4gICAgQ29uc3VtZXJDb25uZWN0b3ItPj5Qcm92aWRlckNvbm5lY3RvcjogcmVxdWVzdCBhcnRpZmFjdFxuICAgIFByb3ZpZGVyQ29ubmVjdG9yLS0-PkNvbnN1bWVyQ29ubmVjdG9yOiByZXR1cm4gYWNjZXNzIGluZm9ybWF0aW9uXG4gICAgYWx0IHByb3RvY29sLlBVTExcbiAgICAgICAgQ29uc3VtZXJCYWNrZW5kLT4-UHJvdmlkZXJCYWNrZW5kOiByZXF1ZXN0IGRhdGFcbiAgICAgICAgUHJvdmlkZXJCYWNrZW5kLS0-PkNvbnN1bWVyQmFja2VuZDogcmV0dXJuIGRhdGFcbiAgICBlbHNlIHByb3RvY29sLlBVU0hcbiAgICAgICAgUHJvdmlkZXJCYWNrZW5kLT4-Q29uc3VtZXJCYWNrZW5kOiBwdXNoIGRhdGFcbiAgICAgICAgQ29uc3VtZXJCYWNrZW5kLT4-Q29uc3VtZXJCYWNrZW5kOiBwcm9jZXNzIGRhdGFcbiAgICBlbmRcblxuICAgICIsIm1lcm1haWQiOnsidGhlbWUiOiJkZWZhdWx0In0sInVwZGF0ZUVkaXRvciI6ZmFsc2UsImF1dG9TeW5jIjp0cnVlLCJ1cGRhdGVEaWFncmFtIjpmYWxzZX0)](https://mermaid-js.github.io/mermaid-live-editor/edit/##eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgcGFydGljaXBhbnQgQ29uc3VtZXJCYWNrZW5kXG4gICAgcGFydGljaXBhbnQgQ29uc3VtZXJDb25uZWN0b3JcbiAgICBwYXJ0aWNpcGFudCBQcm92aWRlckNvbm5lY3RvclxuICAgIHBhcnRpY2lwYW50IFByb3ZpZGVyQmFja2VuZFxuICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-UHJvdmlkZXJDb25uZWN0b3I6IHF1ZXJ5IGRhdGEgb2ZmZXJzXG4gICAgUHJvdmlkZXJDb25uZWN0b3ItLT4-Q29uc3VtZXJDb25uZWN0b3I6IHJldHVybiBtZXRhZGF0YVxuICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-UHJvdmlkZXJDb25uZWN0b3I6IGNvbnRyYWN0IHJlcXVlc3RcbiAgICBQcm92aWRlckNvbm5lY3Rvci0tPj5Db25zdW1lckNvbm5lY3RvcjogY29udHJhY3QgYWdyZWVtZW50XG4gICAgQ29uc3VtZXJDb25uZWN0b3ItPj5Qcm92aWRlckNvbm5lY3RvcjogcmVxdWVzdCBhcnRpZmFjdFxuICAgIFByb3ZpZGVyQ29ubmVjdG9yLS0-PkNvbnN1bWVyQ29ubmVjdG9yOiByZXR1cm4gYWNjZXNzIGluZm9ybWF0aW9uXG4gICAgYWx0IHByb3RvY29sLlBVU0hcbiAgICAgICAgUHJvdmlkZXJCYWNrZW5kLT4-Q29uc3VtZXJCYWNrZW5kOiBwdXNoIGRhdGFcbiAgICAgICAgQ29uc3VtZXJCYWNrZW5kLT4-Q29uc3VtZXJCYWNrZW5kOiBwcm9jZXNzIGRhdGFcbiAgICBlbmRcblxuICAgICIsIm1lcm1haWQiOiJ7XG4gIFwidGhlbWVcIjogXCJkZWZhdWx0XCJcbn0iLCJ1cGRhdGVFZGl0b3IiOmZhbHNlLCJhdXRvU3luYyI6dHJ1ZSwidXBkYXRlRGlhZ3JhbSI6ZmFsc2V9)

This way, a consumer still has to negotiate a contract to get access to the provider's data and also
policies can be enforced for the access data. After that, the consumer can take the provided
information and establish a connection directly between the provider's system acting as a data
source, and a system on the consumer-side acting as the data sink.
This offers the possibility to establish and leave connections open, or to switch from data pulling
to data pushing. Requirements regarding data volume and transfer in real time can thus be ensured by
corresponding systems and are not limited by the Dataspace Connector.

---

**Note**: Policies can only be enforced for data that is exchanged via IDS protocol. Thus, as a
consumer, make sure that your system, that is responsible for the actual data exchange, is aware of
the usage policies and can ensure its compliance.
