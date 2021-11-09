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
separate implementation has to be made for each possible protocol.

_The repository [DSC Camel Instance](https://github.com/International-Data-Spaces-Association/DSC-Camel-Instance)
describes how to use Apache Camel together with the Dataspace Connector and gives examples for
connecting different backend types. - This repo is archived._

This page describes the steps necessary to create a route which will be deployed in Camel using the
Dataspace Connector's API. The Camel routes are meant to transfer data from a backend to the
connector or vice versa. The following example explains how to create and use routes for fetching
and dispatching data **since v7.x.x**.

---

**Note**: The JSON used in the example's steps has been kept short and may contain more fields.

---

## Create a route for fetching data

### Fetch data from HTTP backend

#### Step 1 (optional): Create a data source

The `DataSource` entity holds additional information required to access a backend. In the case of
an HTTP backend (e.g. a REST API), this additional information consists solely of the access
credentials for the backend. Therefore, if the backend does not require authentication, you can
skip this step. If the backend requires authentication, you can either add basic authentication
credentials or an authentication header. To create a data source, make a request to
`POST /api/datasource` with a JSON body as follows:

To configure basic authentication:
```json
{
  "basicAuth": {
    "key": "[username]",
    "value": "[password]"
  },
  "type": "REST"
}
```

To configure an authentication header:
```json
{
  "apiKey": {
    "key": "[header-name]",
    "value": "[header-value]"
  },
  "type": "REST"
}
```

The response will contain a description of the created data source. The description contains a link
with reference *self*, from which you should copy and store the UUID, as this will be used to link
the data source to an endpoint.

#### Step 2: Create a generic endpoint

`GenericEndpoints` represent backends. Therefore, you have to create a generic endpoint for the
backend from which you want to fetch data. To create a generic endpoint for an HTTP backend,
send a request to the endpoint `POST /api/endpoints` using the following JSON:

```json
{
  "location": "[backend-url]",
  "type": "GENERIC"
}
```

The response will contain the description of the created endpoint, from which you copy and store
the UUID found in the self-link.

---

**Note**: The specified URL will be called using HTTP GET.

---

#### Step 3 (optional): Link the data source to the generic endpoint

If you did not execute [Step 1](#step-1-optional-create-a-data-source), you can skip this step, too.

After the data source and the generic endpoint have been created, they have to be linked, so that
the authentication information from the data source is used when making a request to the URL
defined in the generic endpoint. To link them, make a request to the following URL after replacing
the previously copied UUIDs:
`PUT /api/endpoints/{generic-endpoint-uuid}/datasource/{datasource-uuid}`

#### Step 4: Create a route

Camel routes are created from `Route` entity instances. Therefore, a route has to be created.
When creating a route, there are two different deploy modes for routes: `Camel` and `None`. When
setting `Camel`, a Camel route can be created and deployed for this route. When setting `None`, no
deployment will take place. To create a route that will be deployed in Camel, make a request to
`POST /api/routes` with the following JSON:

```json
{
  "title": "My route title",
  "deploy": "Camel"
}
```

The response will contain the description of the created route, from which you copy and store the
self-link.

#### Step 5: Link the generic endpoint to the route

The previously created route does not yet have a start defined. To set the start endpoint, make a
request to`POST /api/routes/{route-uuid}/endpoint/start`, where `route-uuid` is replaced with the
UUID from the route's self-link, and add the following input in the request body after replacing
the generic endpoint's UUID:

```
"[generic-endpoint-uuid]"
```

#### Step 6: Create an artifact

Once the route start is set, the route is ready to be linked to an artifact. Note, that the end of
the route does not have to be defined, as the route will be triggered on request and return the
data fetched.

To create an artifact that is linked to a route, simply use the route's self-link as the artifact's
access URL. To create the artifact, make a request to `POST /api/artifacts` with the following JSON
in the request body:

```json
{
  "title": "My artifact title",
  "accessUrl": "[route-self-link]"
}
```

As the response you will see the description of the artifact that has been created. The description
contains a link with reference `data`. Copy this link to request the data.

---

**Note**: You do not need to add any authentication information when creating the artifact, as
authentication information is provided through the datasource.

---

#### Step 7: Fetch the data

To fetch data via the created route, simply make a GET request to the previously copied data-link
of the artifact. The response should contain the data returned by the HTTP backend you configured
for the generic endpoint.

---

**Note**: Parametrized requests are not supported when the data is fetched via a route.

---

### Fetch data from database

#### Step 1: Create a data source

The `DataSource` entity holds additional information required to access a backend. In the case of
a database, it holds the full access information to the database. To create a data source for a
database, make a request to `POST /api/datasource` with a JSON body as follows:


```json
{
  "basicAuth": {
    "key": "[username]",
    "value": "[password]"
  },
  "type": "DATABASE",
  "url": "[jdbc-url]",
  "driverClassName": "[driver-class]"
}
```

The response will contain a description of the created data source. The description contains a link
with reference *self*, from which you should copy and store the UUID, as this will be used to link
the data source to an endpoint.

---

**Note**: The full name of the driver class is required, so e.g. `org.h2.Driver` or
`org.postgresql.Driver`.

---

#### Step 2: Create a generic endpoint

`GenericEndpoints` represent backends. Therefore, you have to create a generic endpoint for the
backend from which you want to fetch data. When creating a generic endpoint for a database (as
opposed to HTTP backends), the access URL does not have to be set in the generic endpoint, as
it is already contained in the datasource. Instead, the `location` of the generic endpoint
has to be a valid URI for the Camel sql component and therefore contain the query, e.g.:

```sql
sql:select * from mytable?initialDelay=10000&delay=15000&useIterator=false
```

More detailed information on how this URI should look and which parameters can be used
can be found in the
[Camel documentation](https://camel.apache.org/components/latest/sql-component.html).

To create a generic endpoint, send a request to the endpoint `POST /api/endpoints` using the
following JSON:

```json
{
  "location": "[Camel SQL component URI (query)]",
  "type": "GENERIC"
}
```

The response will contain the description of the created endpoint, from which you copy and store
the UUID found in the self-link.

#### Steps 3 to 7

Execute steps 3 to 7 of [Fetch data from HTTP backend](#fetch-data-from-http-backend).

The response received in step 7 should contain the data returned by your database when the query
defined in the generic endpoint is executed.

### Fetch data from other backend types

It is also possible to use other Camel components than HTTP and SQL in the routes. The `location`
attribute of a generic endpoint will be pasted one-to-one into the `<to uri="..."/>` in the
Camel route. For detailed information on how to use a Camel component in a route, have a look at
the respective component's documentation.

There are some Camel components that behave differently when used in `from` or `to` tags.
An example is the File component. It will read a file when used in the `from` tag and write to
a file when used in a `to` tag. As the endpoint's location is always inserted into a `to` tag, the
File component can therefore not be used to read a file in a route generated by the connector.

If you want to use a component which can only be used in the desired way in a `from` tag, you
have to create and deploy the Camel route manually. Therefore, first create a local artifact by
sending a request with the following JSON body to `POST /api/artifacts`:

```json
{
  "title": "My artifact title",
  "value": "value to be overridden"
}
```

Then you can copy the created artifact's data-link and use this to define a Camel route that
fetches data via the desired component and pushes it to the data link, e.g.

```xml
<routes xmlns="http://camel.apache.org/schema/spring">
    <route id="my-custom-route">

        <from uri="file://directory?fileName=file-to-read.txt"/>
        <convertBodyTo type="java.lang.String"/>

        <setHeader name="CamelHttpMethod"><constant>PUT</constant></setHeader>
        <setHeader name="Authorization"><constant>Basic YWRtaW46cGFzc3dvcmQ=</constant></setHeader>
        <to uri="[the artifact's data link]"/>

    </route>
</routes>
```

Finally, deploy this route in the connector as described [here](#deploying-camel-routes-from-xml-files).

---

**Note**: Per default, only the HTTP and SQL Camel component are present in the Dataspace
Connector. In order to use any other Camel component, e.g. File, the dependency for that
component has to be added to the `pom.xml` first!

---

## Create a route for dispatching data

### Dispatch data to an HTTP backend

#### Steps 1 to 4

Execute steps 1 to 4 of [Fetch data from HTTP backend](#fetch-data-from-http-backend).

#### Step 5: Link the generic endpoint to the route

The previously created route does not yet have an end defined. To set the end endpoint, make a
request to `POST /api/routes/[route-uuid]/endpoint/end`, where `[route-uuid]` is replaced with the
UUID from the route's self-link, and add the following input in the request body after replacing
the generic endpoint's UUID:

```
"[generic-endpoint-uuid]"
```

#### Step 6: Create or request an artifact

Once the route end is set, the route is ready to be used for dispatching data. Note, that the
start of the route does not have to be defined, as the route will be triggered on request and use
the artifact's data as the initial input.

In order to dispatch any data via a route, an artifact needs to be present that holds the data.
For the sake of simplicity, in this tutorial an artifact will be created for that. In a real
scenario, the artifact will most likely be one that has been requested from another connector.

To create a local artifact, make a request to `POST /api/artifacts` with the following JSON
in the request body:

```json
{
  "title": "My artifact title",
  "value": "test data value"
}
```

As the response you will see the description of the artifact that has been created. The description
contains a link with reference `data`. Copy this link to request the data.

#### Step 7: Dispatch the data

To dispatch the artifact's data via the created route, you have to request the artifact's data
using the previously copied data-link. When making a request to that link, you can specify
the query parameter `routeIds`, which takes a list of one or more route URIs. Therefore, use
the route's self-link that you copied in this parameter. When executing the request, the
data of the artifact will be dispatched via all specified routes, before the data is returned.
Thus, in the backend specified in the generic endpoint, you will receive the artifact's data.
When multiple routes are specified and one of them runs into an error, the following routes
will not be called anymore, and the data will not be returned; instead, you're going to see
an error response.

---

**Note**: When getting the data of an artifact that has been requested from another connector, the
received data will not be stored in the local database of the connector, as long as at least one
route is specified. If you want to store the data in the local database, you have to request it
once without specifying any route. If you do not want to the data to be stored in the local
database, set `download=false` when requesting a contract and always specify at least one route
when requesting the data via the `GET /api/artifact/{id}/data` endpoint.

---

### Dispatch data to other backend types

Same as when fetching data, other Camel components than HTTP can be used in routes for dispatching
data. The `location` attribute of a generic endpoint will be pasted one-to-one into the
`<to uri="..."/>` in the Camel route. For detailed information on how to use a Camel component
in a route, have a look at the respective component's documentation.

---

**Note**: Per default, only the HTTP and SQL Camel component are present in the Dataspace
Connector. In order to use any other Camel component, e.g. File, the dependency for that component
has to be added to the `pom.xml` first!

---

## Use routes in subscriptions

Routes can also be used in subscriptions. For this, a route (including the generic endpoint) has
to be created as described in the chapter
[Create a route for dispatching data](#create-a-route-for-dispatching-data). Then, when creating a
new subscription, that route's self-link can be set as the `location` of the subscription. To
create a subscription using the route, make a request to `POST /api/subscriptions` with the
following request body:

```json
{
  "title": "My subscription title",
  "target": "[subscription-target]",
  "location": "[route-self-link]",
  "subscriber": "[subscriber name]",
  "pushData": true
}
```

Now, whenever there is an update to the subscription target, the update information (only headers,
if `pushData=false`; headers and data in the request body, if `pushData=true`) will be sent via
the specified route.

## Use apps in routes

The above examples show simple routes which only call one endpoint. However, using Camel routes to
fetch or dispatch data also provides the possibility of more complex data flows that include
data apps. Once an app has been downloaded from the AppStore and deployed, it can be used in
routes. After the app has been downloaded, the endpoints available for the app have been
added as `AppEndpoints`. You can have a look at them by making a request to `GET /api/endpoints`.
These app endpoints can be used in routes in the same way that generic endpoints are (as start
and end of a route). Of course, when using apps, you probably want to integrate the apps
into the middle of the route, so they would be neither start nor end. This is where sub-routes
come into play.

Each route can have any number of sub-routes. **Sub-routes are created in the same way routes
are (`POST /api/routes`) but should have deploy mode `None`, as they should not be deployed on
their own but only as part of another route.** You can set endpoints as the start and end of
a sub-route, and add the sub-routes to a main route by making a request to
`POST /api/routes/[main-route-uuid]/steps` with the following request body:

```json
{
  "[self-link of the sub-route]"
}
```

---

**Note**:
Apps can also be used as the start and/or end of a route, but note that the corresponding
Camel routes will still run timer-based, as they cannot be triggered upon request.

---

### Example

Let's say you have a backend, for which you have created a generic endpoint. You have also
downloaded and deployed an app that has one input and one output endpoint (meaning you
call the input endpoint first and then call the output endpoint to retrieve the results).
Which endpoint is which can be determined by the `endpointType` attribute of the app endpoints.
No you want to create a route that fetches data from the backend and routes it through the app
before returning it.

#### Step 1: Create the first sub-route

Create the first sub-route (route with deploy mode `None`). Then, add the generic endpoint as
the sub-route's start, and the app's input endpoint as the sub-route's end as described in the
previous chapters.

#### Step 2: Create the second sub-route

Create the second sub-route (route with deploy mode `None`). Then, add the app's output endpoint
as the sub-route's start. The end of the second sub-route does not have to be defined, since
the result of the route will be returned when the route is triggered.

#### Step 3: Create the main route

Create the main route (route with deploy mode `Camel`) and add the generic endpoint as the
route's start. Then, add the first and the second sub-route to the main route by making
a request to `POST /api/routes/{main-route-uuid}/steps` with the following request body:

```json
{
  "[self-link of the first sub-route]",
  "[self-link of the second sub-route]"
}
```

#### Step 4: Use the route

Now, you can link the route to an artifact as described in the previous sections. When you request
the artifact's data, the data will be routed through the app before being returned. Of course, you
can also use apps in the same way in routes for dispatching data.

## Deploying Camel routes from XML files

The API for deploying Camel routes from XML files, that was previously offered by the
[DSC Camel Instance](https://github.com/International-Data-Spaces-Association/DSC-Camel-Instance),
has been integrated into the Dataspace Connector, which now provides the following endpoints for
deploying and removing Camel routes and, if necessary, required beans:

* **POST /api/camel/routes** (*multipart/form-data* with part *file* for the XML file): add routes
* **DELETE /api/camel/routes/{route-id}**: remove route by ID
* **POST /api/beans** (*multipart/form-data* with part *file* for the XML file): add beans
* **DELETE /api/beans/{bean-id}**: remove bean by ID

The files sent to the application should have the following structures for routes and beans
respectively:

```xml
<routes xmlns="http://camel.apache.org/schema/spring">

    <route id="...">
        ...
    </route>

</routes>
```

```xml
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
