---
layout: default
title: Camel
nav_order: 6
description: ""
permalink: /CommunicationGuide/v6/Camel
parent: Communication Guide
---

# Using Apache Camel
{: .fs-9 }

Here, you can find instructions for using Apache Camel with the Dataspace Connector.
{: .fs-6 .fw-300 }

---

The communication between the Dataspace Connector and data apps can be achieved by using an
integration Framework like [Apache Camel](https://camel.apache.org/). This also provides the
possibility to use all kinds of different backends for resources registered in the Connector, as no
separate implementation has to be made for each possible protocol. To keep the Dataspace Connector
lightweight and modular, no integration framework will be integrated directly, but rather be
executed standalone in parallel to the Connector's core container.

_The repository [DSC Camel Instance](https://github.com/International-Data-Spaces-Association/DSC-Camel-Instance)
describes how to use Apache Camel together with the Dataspace Connector and gives examples for
connecting different backend types. - This repo is archived._

This page describes the steps necessary to create a route which will be deployed in Camel using the
Dataspace Connector's API. The Camel routes are meant to transfer data from a backend to the
connector or vice versa. The following example explains how to create a route that pushes data to
the connector. At the bottom of this section, you can find a chapter explaining which steps need to
be executed differently to create a route that fetches data from the connector.

---

**Note**: The JSON used in the example's steps has been kept short and may contain more fields.

---

## How to create a route that pushes data to the connector

### Step 1: Create an artifact

As the Camel route's source or destination is always an artifact's `data`-endpoint, the first step
is to create an artifact. This can be done by sending the following JSON to `POST /api/artifacts`:

```json
{
  "title": "My artifact",
  "value": "value to be overridden"
}
```

As the response you will see the description of the artifact that has been created. The description
contains a link with reference `data`. Copy this link, as you will need it in the following steps.

### Step 2: Create the endpoints

The endpoints called in the Camel routes are represented by `Endpoint` objects in the Dataspace
Connector's data model. Therefore, at least two endpoint objects have to be created: one for the
route's source and one for the route's destination.

#### Step 2.1: Create a generic endpoint

Generic endpoints represent backends. Therefore, you have to create a generic endpoint for the
backend from which you want to fetch data. In this example, this will be a backend located at
`https://backend.com`. Send a request to the endpoint `POST /api/endpoints` using the following
JSON:

```json
{
  "location": "https://backend.com",
  "type": "GENERIC"
}
```

The response will contain the description of the created endpoint, from which you copy and store the
UUID found in the self-link.

---

**Note**: The specified URL will be called using HTTP GET.

---

#### Step 2.2: Create a connector endpoint

Connector endpoints represent the connector's endpoints where data can be pushed to or fetched from.
So you need to create a connector endpoint that points to the previously created artifact's `data`
endpoint. Therefore, make a request to `POST /api/endpoints` with the following JSON, where the
`location` has to be replaced with the URI copied at the end of [Step 1](#step-1-create-an-artifact):

```json
{
  "location": "https://localhost:8080/api/artifacts/f32d6aaa-ccfd-4b48-a305-ddb4222c89f0/data",
  "type": "CONNECTOR"
}
```

The response will contain the description of the created endpoint, from which you copy and store the
UUID found in the self-link.

### Step 3: Create a route

After both required endpoints have been created, you can now create the route itself and then link
it to the endpoints.

#### Step 3.1: Create the route

When creating a route, there are two different deploy modes for routes: `Camel` and `None`. When
setting `Camel`, a Camel route will be created and deployed for this route. When setting `None`, no
deployment will take place. To create a route that will be deployed in Camel, make a request to
`POST /api/routes` with the following JSON:

```json
{
  "title": "My route",
  "deploy": "Camel"
}
```

The response will contain the description of the created route, from which you copy and store the
UUID found in the self-link.

#### Step 3.2: Add the route's start

The previously created route does not yet have a start defined. To set the start endpoint, make a
request to`POST /api/routes/{id}/endpoint/start`, where `{id}` is replaced with the route's UUID
copied in [Step 3.1](#step-31-create-the-route), and add the following input in the request body,
where you insert the generic endpoint's UUID copied in
[Step 2.1](#step-21-create-a-generic-endpoint): `930fcdae-af99-47a6-8bfa-e4aa645abcd6`.

#### Step 3.3: Add the route's end

The previously created route does not yet have an end defined. To set the end endpoint, make a
request to`POST /api/routes/{id}/endpoint/end`, where `{id}` is replaced with the route's UUID
copied in[Step 3.1](#step-31-create-the-route), and add the following input in the request body,
where you insert the connector endpoint's UUID copied in
[Step 2.2](#step-22-create-a-connector-endpoint): `75207cbc-6721-4209-9a76-1d1f5b30a157`.

### Step 4: Request the artifact's data

After the route's start and end have been added, a Camel route will automatically be generated and
deployed. To see that the route is active and working, request the data of the artifact that has
been created in[Step 1](#step-1-create-an-artifact) by making a `GET` request to the `data` endpoint
URI copied in that step. The data returned by the connector will be the same data that you receive
by making a direct call to the backend used in [Step 2.1](#step-21-create-a-generic-endpoint).

## How to create a route that fetches data from the connector

The above example shows how to create a route that pushes data from a backend to the connector. It
is also possible to create a route that fetches data from the connector and pushes it to a backend.
The steps to achieve this are essentially the same as in the example above. The only difference is
that you have to set the connector endpoint as the route's start and the generic endpoint as the
route's end.

---

**Note**: In this case the URL specified for the generic endpoint will be called using HTTP POST.

## Deploying Camel routes from XML files

The API for deploying Camel routes from XML files, that was previously offered by the
[DSC Camel Instance](https://github.com/International-Data-Spaces-Association/DSC-Camel-Instance),
has been integrated into the Dataspace Connector, which now provides the following endpoints for
deploying and removing Camel routes and, if necessary, required beans:

* **POST /api/camel/routes** (*multipart/form-data* with part *file* for the XML file): add routes
* **DELETE /api/camel/routes/{route-id}**: remove route by ID
* **POST /api/beans** (*multipart/form-data* with part *file* for the XML file): add beans
* **DELETE /api/beans/{bean-id}**: remove bean by ID

The files sent to the application should have the following structures for routes and beans respectively:

```
<routes xmlns="http://camel.apache.org/schema/spring">

    <route id="...">
        ...
    </route>

</routes>
```

```
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

    <bean id="..." class="...">
        ...
    </bean>

</beans>
```

More information and examples on XML Camel routes can be found in the *DSC Camel Instance* repository.
