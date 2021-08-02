---
layout: default
title: Subscription
nav_order: 5
description: ""
permalink: /CommunicationGuide/v6/Subscription
parent: Communication Guide
---

# Subscription
{: .fs-9 }

See how to subscribe to resources as IDS or non-IDS system.
{: .fs-6 .fw-300 }

---

Using the Dataspace Connector, the data exchange between data provider and consumer is always
initiated by the consumer connector asking for metadata and then pulling the data. In some use
cases, the data on the provider side is regularly updated. These updates must be propagated all the
way to the consumer, both connector and data processing service in the background.

The consumer already has the ability to send parameterized queries to potentially filter the
retrieved data, e.g. for a specific time period. Nevertheless, until Dataspace Connector v5, the
consumer has to periodically pull to detect a data update because there was no way for the consumer
connector to notify a connected backend in an event-driven manner. Also, notifying about updates
from provider to consumer connector was quite a complicated task to be done by manually triggering
the sending of IDS `ResourceUpdateMessages`.

The subscription process implemented in v6 actually does the same thing. However, a provider backend
does not need to remember which consumer has subscribed and who needs to be notified at which
address. Instead, the Dataspace Connector persists this information and sends out notifications
automatically.

[![](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgcGFydGljaXBhbnQgUHJvdmlkZXJCYWNrZW5kXG4gICAgcGFydGljaXBhbnQgVXNlclxuICAgIHBhcnRpY2lwYW50IFByb3ZpZGVyQ29ubmVjdG9yXG4gICAgcGFydGljaXBhbnQgQ29uc3VtZXJDb25uZWN0b3JcbiAgICBwYXJ0aWNpcGFudCBDb25zdW1lckJhY2tlbmRcbiAgICBVc2VyLT4-UHJvdmlkZXJDb25uZWN0b3I6IGNyZWF0ZSBvZmZlcmVkIHJlc291cmNlXG4gICAgUHJvdmlkZXJDb25uZWN0b3ItLT4-VXNlcjogY3JlYXRlZFxuICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-UHJvdmlkZXJDb25uZWN0b3I6IHJlcXVlc3QgcmVzb3VyY2VcbiAgICBQcm92aWRlckNvbm5lY3Rvci0tPj5Db25zdW1lckNvbm5lY3RvcjogbWV0YWRhdGFcbiAgICBDb25zdW1lckNvbm5lY3Rvci0-PkNvbnN1bWVyQ29ubmVjdG9yOiBjcmVhdGUgcmVxdWVzdGVkIHJlc291cmNlXG4gICAgQ29uc3VtZXJCYWNrZW5kLT4-Q29uc3VtZXJDb25uZWN0b3I6IHN1YnNjcmliZSB0byByZXF1ZXN0ZWQgcmVzb3VyY2VcbiAgICBDb25zdW1lckNvbm5lY3Rvci0tPj5Db25zdW1lckJhY2tlbmQ6IG9rXG4gICAgUHJvdmlkZXJCYWNrZW5kLT4-UHJvdmlkZXJDb25uZWN0b3I6IGRhdGEgdXBkYXRlIGV2ZW50IChQT1NUIC9ub3RpZnkpXG4gICAgbG9vcCBub3RpZnkgc3Vic2NyaWJlcnNcbiAgICAgICAgUHJvdmlkZXJDb25uZWN0b3ItPj5Db25zdW1lckNvbm5lY3Rvcjogc2VuZCByZXNvdXJjZSB1cGRhdGUgbWVzc2FnZVxuICAgICAgICBDb25zdW1lckNvbm5lY3Rvci0-PkNvbnN1bWVyQ29ubmVjdG9yOiB1cGRhdGUgcmVxdWVzdGVkIHJlc291cmNlXG4gICAgICAgIENvbnN1bWVyQ29ubmVjdG9yLS0-PlByb3ZpZGVyQ29ubmVjdG9yOiBwcm9jZXNzZWRcbiAgICBlbmRcbiAgICBQcm92aWRlckNvbm5lY3Rvci0tPj5Qcm92aWRlckJhY2tlbmQ6IG9rXG4gICAgbG9vcCBub3RpZnkgc3Vic2NyaWJlcnNcbiAgICAgICAgYWx0IHB1c2hEYXRhXG4gICAgICAgICAgICBDb25zdW1lckNvbm5lY3Rvci0-PlByb3ZpZGVyQ29ubmVjdG9yOiByZXF1ZXN0IGRhdGFcbiAgICAgICAgICAgIFByb3ZpZGVyQ29ubmVjdG9yLS0-PkNvbnN1bWVyQ29ubmVjdG9yOiBkYXRhXG4gICAgICAgICAgICBDb25zdW1lckNvbm5lY3Rvci0-PkNvbnN1bWVyQmFja2VuZDogc2VuZCBub3RpZmljYXRpb24gd2l0aCBkYXRhXG4gICAgICAgICAgICBDb25zdW1lckJhY2tlbmQtLT4-Q29uc3VtZXJDb25uZWN0b3I6IG9rXG4gICAgICAgIGVsc2VcbiAgICAgICAgICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-Q29uc3VtZXJCYWNrZW5kOiBzZW5kIG5vdGlmaWNhdGlvbiB3aXRob3V0IGRhdGFcbiAgICAgICAgICAgIENvbnN1bWVyQmFja2VuZC0tPj5Db25zdW1lckNvbm5lY3Rvcjogb2tcbiAgICAgICAgZW5kICAgIFxuICAgIGVuZFxuIiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZSwiYXV0b1N5bmMiOnRydWUsInVwZGF0ZURpYWdyYW0iOmZhbHNlfQ)](https://mermaid-js.github.io/mermaid-live-editor/edit/##eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgcGFydGljaXBhbnQgUHJvdmlkZXJCYWNrZW5kXG4gICAgcGFydGljaXBhbnQgVXNlclxuICAgIHBhcnRpY2lwYW50IFByb3ZpZGVyQ29ubmVjdG9yXG4gICAgcGFydGljaXBhbnQgQ29uc3VtZXJDb25uZWN0b3JcbiAgICBwYXJ0aWNpcGFudCBDb25zdW1lckJhY2tlbmRcbiAgICBVc2VyLT4-UHJvdmlkZXJDb25uZWN0b3I6IGNyZWF0ZSBvZmZlcmVkIHJlc291cmNlXG4gICAgUHJvdmlkZXJDb25uZWN0b3ItLT4-VXNlcjogY3JlYXRlZFxuICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-UHJvdmlkZXJDb25uZWN0b3I6IHJlcXVlc3QgcmVzb3VyY2VcbiAgICBQcm92aWRlckNvbm5lY3Rvci0tPj5Db25zdW1lckNvbm5lY3RvcjogbWV0YWRhdGFcbiAgICBDb25zdW1lckNvbm5lY3Rvci0-PkNvbnN1bWVyQ29ubmVjdG9yOiBjcmVhdGUgcmVxdWVzdGVkIHJlc291cmNlXG4gICAgQ29uc3VtZXJCYWNrZW5kLT4-Q29uc3VtZXJDb25uZWN0b3I6IHN1YnNjcmliZSB0byByZXF1ZXN0ZWQgcmVzb3VyY2VcbiAgICBDb25zdW1lckNvbm5lY3Rvci0tPj5Db25zdW1lckJhY2tlbmQ6IG9rXG4gICAgUHJvdmlkZXJCYWNrZW5kLT4-UHJvdmlkZXJDb25uZWN0b3I6IGRhdGEgdXBkYXRlIGV2ZW50IChQT1NUIC9ub3RpZnkpXG4gICAgbG9vcCBub3RpZnkgc3Vic2NyaWJlcnNcbiAgICAgICAgUHJvdmlkZXJDb25uZWN0b3ItPj5Db25zdW1lckNvbm5lY3Rvcjogc2VuZCByZXNvdXJjZSB1cGRhdGUgbWVzc2FnZVxuICAgICAgICBDb25zdW1lckNvbm5lY3Rvci0-PkNvbnN1bWVyQ29ubmVjdG9yOiB1cGRhdGUgcmVxdWVzdGVkIHJlc291cmNlXG4gICAgICAgIENvbnN1bWVyQ29ubmVjdG9yLS0-PlByb3ZpZGVyQ29ubmVjdG9yOiBwcm9jZXNzZWRcbiAgICBlbmRcbiAgICBQcm92aWRlckNvbm5lY3Rvci0tPj5Qcm92aWRlckJhY2tlbmQ6IG9cbiAgICBsb29wIG5vdGlmeSBzdWJzY3JpYmVyc1xuICAgICAgICBhbHQgcHVzaERhdGFcbiAgICAgICAgICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-UHJvdmlkZXJDb25uZWN0b3I6IHJlcXVlc3QgZGF0YVxuICAgICAgICAgICAgUHJvdmlkZXJDb25uZWN0b3ItLT4-Q29uc3VtZXJDb25uZWN0b3I6IGRhdGFcbiAgICAgICAgICAgIENvbnN1bWVyQ29ubmVjdG9yLT4-Q29uc3VtZXJCYWNrZW5kOiBzZW5kIG5vdGlmaWNhdGlvbiB3aXRoIGRhdGFcbiAgICAgICAgICAgIENvbnN1bWVyQmFja2VuZC0tPj5Db25zdW1lckNvbm5lY3Rvcjogb2tcbiAgICAgICAgZWxzZVxuICAgICAgICAgICAgQ29uc3VtZXJDb25uZWN0b3ItPj5Db25zdW1lckJhY2tlbmQ6IHNlbmQgbm90aWZpY2F0aW9uIHdpdGhvdXQgZGF0YVxuICAgICAgICAgICAgQ29uc3VtZXJCYWNrZW5kLS0-PkNvbnN1bWVyQ29ubmVjdG9yOiBva1xuICAgICAgICBlbmQgICAgXG4gICAgZW5kXG4iLCJtZXJtYWlkIjoie1xuICBcInRoZW1lXCI6IFwiZGVmYXVsdFwiXG59IiwidXBkYXRlRWRpdG9yIjpmYWxzZSwiYXV0b1N5bmMiOnRydWUsInVwZGF0ZURpYWdyYW0iOmZhbHNlfQ)

The subscription mechanism follows webhook patterns: a subscriber creates a request that includes
where a response is expected. The current implementation does not allow defining a protocol. Per
default, http POST requests are made.

---

**Note**: If you would like to keep an open connector for receiving updates via e.g. MQTT, a custom
service could offer this functionality and listen to events from the Dataspace Connector, as this
does not offer such a functionality by now.

---

In the Dataspace Connector, a subscription can be created by using a json object:

```json
{
  "title": "string",
  "description": "string",
  "target": "uri",
  "location": "uri",
  "subscriber": "uri",
  "pushData": true
}
```

A `title` and `description` offer further information and can be added optionally. The `target`
points to the object that should be watched. As an IDS subscriber, this can be an offer, a
representation, or an artifact. As a non-IDS subscriber, subscriptions can be created for requests,
representations, or artifacts.

The `location` represents e.g. a URL that expects the notification. The `subscriber` URI uniquely
identifies the subscribing system. The boolean `pushData` states whether the data should be directly
pushed to the subscriber or not.

## Example

In the following example, it is assumed that the contract negotiation and an initial data exchange
has already been set up. So how to propagate a data update from the provider's backend to the
consumer's backend now?

### Step 1: Create a non-IDS subscription

A custom service on the consumer side should be notified about updates of requested resources. The
connector's administrator does not want the application or service to access all REST endpoints.
Thus, the application uses the application account defined in the `application.properties`.

```properties
spring.security.app.name=...
spring.security.app.password=...
```

Subscriptions can be managed with the provided CRUD endpoints.

![Swagger Subscription Endpoints](../../../assets/images/v6/swagger_subscriptions.png)

Subscriptions will be automatically added to the respective target entity via a POST request. As
described above, subscriptions can point at requests, representations, and artifacts. All
subscriptions for an entity can be accessed via a separate endpoint.

Create subscription:

```
curl -X 'POST' \
  'https://localhost:8080/api/subscriptions' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "Example",
  "description": "Notify on update",
  "target": "https://localhost:8080/api/requests/3660e95a-41bf-488b-8bff-3bfe7c19b633",
  "location": "https://backend/event",
  "subscriber": "https://customBackend",
  "pushData": true
}'
```

Response:

```json
{
  "creationDate": "2021-08-02T14:30:31.921+0200",
  "modificationDate": "2021-08-02T14:30:31.921+0200",
  "target": "https://localhost:8080/api/requests/3660e95a-41bf-488b-8bff-3bfe7c19b633",
  "location": "https://backend/event",
  "subscriber": "https://customBackend",
  "pushData": true,
  "idsProtocol": false,
  "additional": {},
  "_links": {
    "self": {
      "href": "https://localhost:8080/api/subscriptions/120665fa-7156-49f5-b0cc-952b1bf64228"
    }
  }
}
```

`GET https://localhost:8080/api/requests/3660e95a-41bf-488b-8bff-3bfe7c19b633/subscriptions`:

```json
{
  "_embedded": {
    "subscriptions": [
      {
        "creationDate": "2021-08-02T14:30:31.921+0200",
        "modificationDate": "2021-08-02T14:30:31.921+0200",
        "target": "https://localhost:8080/api/requests/3660e95a-41bf-488b-8bff-3bfe7c19b633",
        "location": "https://backend/event",
        "subscriber": "https://customBackend",
        "pushData": true,
        "idsProtocol": false,
        "additional": {},
        "_links": {
          "self": {
            "href": "https://localhost:8080/api/subscriptions/120665fa-7156-49f5-b0cc-952b1bf64228"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "https://localhost:8080/api/requests/3660e95a-41bf-488b-8bff-3bfe7c19b633/subscriptions?page=0&size=30"
    }
  },
  "page": {
    "size": 30,
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  }
}
```

### Step 2: Create an IDS subscription

For subscribing as an IDS consumer connector to an IDS provider connector, other endpoints are
provided:

![Swagger IDS Subscription Endpoints](../../../assets/images/v6/swagger_ids_subscriptions.png)

As a body, the same subscription object is expected. As described above, IDS subscribers can
subscribe to offers, representations, and artifacts.

```
curl -X 'POST' \
  'https://localhost:8080/api/ids/subscribe?recipient=https%3A%2F%2Flocalhost%3A8080%2Fapi%2Fids%2Fdata' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "IDS Example",
  "description": "Notify on update",
  "target": "https://localhost:8080/api/offers/3660e95a-41bf-488b-8bff-3bfe7c19b633",
  "location": "https://localhost:8080/api/ids/data",
  "subscriber": "https://localhost:8080",
  "pushData": false
}'
```

Response: `200 Successfully subscribed to ...`

For IDS communication, custom IDS `RequestMessages` are used. An example is shown below:

Header:
```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:RequestMessage",
  "@id" : "https://w3id.org/idsa/autogen/requestMessage/38b56ba4-1fc1-4dae-9775-7c7d76941dc6",
  "ids:recipientConnector" : [ {
    "@id" : "https://localhost:8080/api/ids/data"
  } ],
  "ids:issuerConnector" : {
    "@id" : "https://localhost:8081"
  },
  "ids:senderAgent" : {
    "@id" : "https://localhost:8081"
  },
  "ids:modelVersion" : "4.1.0",
  "ids:issued" : {
    "@value" : "2021-08-02T14:47:51.467+02:00",
    "@type" : "http://www.w3.org/2001/XMLSchema#dateTimeStamp"
  },
  "ids:securityToken" : {
    "@type" : "ids:DynamicAttributeToken",
    "@id" : "https://w3id.org/idsa/autogen/dynamicAttributeToken/1bf4a886-2658-448a-bfe9-cbc3fafd7a18",
    "ids:tokenFormat" : {
      "@id" : "https://w3id.org/idsa/code/JWT"
    },
    "ids:tokenValue" : "..."
  },
  "ids:target" : {
    "@id" : "https://localhost:8080/api/offers/3660e95a-41bf-488b-8bff-3bfe7c19b633"
  }
}
```

Payload:

```json
{
  "title":"IDS Example",
  "description":"Notify on update",
  "target":"https://localhost:8080/api/offers/3660e95a-41bf-488b-8bff-3bfe7c19b633",
  "location":"https://localhost:8081/api/ids/data",
  "subscriber":"https://localhost:8081",
  "pushData":false
}
```

On the provider side, the `subscriber` is set to the message's `issuerConnector` and the boolean
`idsProtocol` is set to true. Then, the subscription is persisted and linked to the target object.
If something went wrong, e.g. the target does not exist, a matching subscription message is
returned.

---

**Hint**: For managing own subscriptions, as a consumer connector, the REST endpoint
`/api/subscriptions/owning` is provided. It returns a list of subscriptions at other IDS connectors.

---

For unsubscribing from an object, the endpoint `/api/ids/unsubscribe` can be used. It only expects
a target URI and automatically send respective IDS messages, as shown below:

```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
  "@type" : "ids:RequestMessage",
  "@id" : "https://w3id.org/idsa/autogen/requestMessage/589324d0-6e1e-4948-9910-6922319ddb9a",
  "ids:recipientConnector" : [ {
    "@id" : "https://localhost:8080/api/ids/data"
  } ],
  "ids:issuerConnector" : {
    "@id" : "https://localhost:8081"
  },
  "ids:senderAgent" : {
    "@id" : "https://localhost:8081"
  },
  "ids:modelVersion" : "4.1.0",
  "ids:issued" : {
    "@value" : "2021-08-02T14:51:08.853+02:00",
    "@type" : "http://www.w3.org/2001/XMLSchema#dateTimeStamp"
  },
  "ids:securityToken" : {
    "@type" : "ids:DynamicAttributeToken",
    "@id" : "https://w3id.org/idsa/autogen/dynamicAttributeToken/3af55841-fb7b-493e-8570-9533ba0a7c8c",
    "ids:tokenFormat" : {
      "@id" : "https://w3id.org/idsa/code/JWT"
    },
    "ids:tokenValue" : "..."
  },
  "ids:target" : {
    "@id" : "https://localhost:8080/api/offers/3660e95a-41bf-488b-8bff-3bfe7c19b633"
  }
}
```

Response: `200 Successfully unsubscribed from ...`

### Step 3: Notify subscribers

As a last step, the subscribers need to be notified on data update events. The Dataspace Connector
offers two possibilities for that:
1. It automatically notifies the subscribers on an update via the REST API. That means, as soon as
   some user or system sends a PUT request to a request, offer, representation, or artifact
   endpoint, the Connector retrieves the list of subscriptions from its database, checks if the
   subscriber wants to be updated via IDS message or not, and sends the respective message.
2. If no metadata update occurs, but maybe the remote data of a connected system has changed anyway,
   the Connector provides an endpoint `/api/notify` to notify all subscribers. Unlike manually
   triggering the ResourceUpdateMessages as described [here](provider.md#resource-updates), the
   Connector does not need any input about who exactly wants to be notified. It gets all information
   from its database as in option 1.

---

**Note**: Automated or manually triggered update events will notify all subscribers to the matching
object, as well as all subscribers of its children.

---

Depending on how the subscription has reached the Dataspace Connector, the object is marked by the
flag `idsProtocol`.

#### IDS Notification

The IDS subscribers will be notified via `ResourceUpdateMessage`. It contains the updated metadata
of a resource so the consumer updates its stored information. The data is not pushed to the
consuming connector as the newest data can automatically being retrieved via API call on
`artifacts/{id}/data/**`.

#### Non-IDS Notification

Non-IDS subscribers will receive an http POST request with the following properties. If the
`pushData` boolean is set to `true`, the notification request will contain the data. If not, this
will remain empty. Header arguments help to process which event happened to which target.
* method = POST
* headers = {`ids-event=UPDATED`, `ids-target=https://...`}
* body = `data` (mediaType = "application/octet-stream")
