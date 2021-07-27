---
layout: default
title: Parametrization
nav_order: 4
description: ""
permalink: /CommunicationGuide/v6/Parametrization
parent: Communication Guide
---

# Parametrized Backend Calls
{: .fs-9 }

See how to use query parameters, path segments, and headers for the backend request on data retrieval.
{: .fs-6 .fw-300 }

[previous version](../v5/parametrization.md)

---

The Dataspace Connector supports the usage of custom headers, query parameters, and path segments
when fetching data from backend systems. Therefore, it is possible for a data consumer to specify
them when requesting data from a data provider.

## Provider

A `resource` has an attribute `endpointDocumentation` of type URI. Thus, when registering a
resource, a data provider can add a link pointing to the API documentation for the referenced
backend. In that documentation, the path segments, query parameters, and headers expected or
supported by the backend API may be described.

When defining the URL of an HTTP backend, the provider can either provide only the base URL
```
http://example-backend.com
```

or already add static path segments or query parameters, if necessary:

```
http://example-backend.com?key=value
```

```
http://example-backend.com/some/path/segments?key=value
```

When a request is made by a consumer specifying additional parameters or path segments, these will
be appended to the pre-defined ones.

## Consumer

After requesting a resource's metadata, a data consumer can follow the link to the documentation,
decide which headers, query parameters, and path segments should be used and then specify these
when requesting the data.

To be able to request the data, the consumer first has to negotiate a contract with the provider for
the desired artifact (using `POST /api/ids/contract`). The consumer connector then automatically
queries the request artifact's and the corresponding resource's metadata and stores them to its
database. The HTTP response contains the contract agreement, that has been created during the
messaging sequence. This agreement object contains some meta information that i.a. reference the IDs
of the newly created artifacts. Using an artifact's ID, the consumer can request the corresponding
data using `GET /api/artifacts/{id}/data` (optionally with the URI of the referring contract agreement
as the `agreementUri` and`download` set to `true`). To this request, any additional path segments,
query parameters, and headers can be added, so that the request may look as follows:

```
GET /api/artifacts/{id}/data/additional/path
GET /api/artifacts/{id}/data?param1=value1&param2=value2
GET /api/artifacts/{id}/data/additional/path?param1=value1&param2=value2
```

The consumer connector sends any additional query parameters, path segments, and headers specified
in the request to `GET /api/artifacts/{id}/data` to the provider connector as the payload of the
automatically sent IDS `ArtifactRequestMessage`.

## Examples

Below, two scenarios are presented and each processing step is explained using example URLs.

### Query Parameters

A data provider wants to share data from a REST API that uses query parameters without having to
create an additional artifact for each possible combination of query parameters. Instead, the data
is being offered using one single artifact and letting the data consumer decide on the query
parameters that should be forwarded.


1. Provider defines the base URL of the REST API as the `accessUrl` of the artifact:
   ```
   http://example-backend.com
   ```
2. Provider provides a link to the REST API documentation as the `endpointDocumentation` attribute
   of a resource offer.
   ```
   Query parameters according to API documentation:
      required: name (any string)
      optional: number (any positive integer)
   ```
3. Consumer follows the link to the REST API documentation after requesting the resource's metadata
   (using `POST /api/ids/description`).
4. Consumer negotiates a contract with the provider for the desired artifact
   (using `POST /api/ids/contract`).
   * The HTTP response contains the contract agreement, that has been created during the
     messaging sequence.
   *  This agreement object contains some meta information that i.a. reference the IDs
     of the newly created artifact(s).
5. Consumer uses the artifact's ID to request the corresponding data, using
   `GET /api/artifacts/{id}/data` (optionally with the URI of the referring contract agreement
   as the `agreementUri` and`download` set to `true`), and adds the query parameters to the request.
    1) only the required parameter
         ```
         GET /api/artifacts/{id}/data?name=John
         ```
    2) the required and the optional parameter
         ```
         GET /api/artifacts/{id}/data?name=John&number=3
         ```
6. The consumer connector sends the additional query parameters specified in the request to
   `GET /api/artifacts/{id}/data` to the provider connector as the payload of the
   `ArtifactRequestMessage`. After receiving the message, the provider connector fetches the data
   from the backend system after appending the specified query parameters to the URL.
    1) `GET http://example-backend.com?name=John`
    2) `GET http://example-backend.com?name=John&number=3`

### Path Segments

A data provider wants to share data from a REST API that supports different path segments without
having to create an additional artifact for each possible path segment or each possible combination
of segments. Instead, the data is being offered using one single artifact and letting the data
consumer decide what path segment(s) should be forwarded.

1. Provider defines the URL of the REST API as the `accessUrl` of the resource's artifact:
   ```
   http://example-backend.com/
   ```
2. Provider provides a link to the REST API documentation as the `endpointDocumentation` attribute
   of a resource offer.
   ```
   Path segments according to API documentation:
        required: name (any string), number (any positive integer)
   ```
3. Consumer follows the link to the REST API documentation after requesting the resource's metadata
   (using `POST /api/ids/description`).
4. Consumer negotiates a contract with the provider for the desired artifact
   (using `POST /api/ids/contract`).
   * The HTTP response contains the contract agreement, that has been created during the
     messaging sequence.
   *  This agreement object contains some meta information that i.a. reference the IDs
      of the newly created artifact(s).
5. Consumer uses the artifact's ID to request the corresponding data, using
   `GET /api/artifacts/{id}/data` (optionally with the URI of the referring contract agreement
   as the `agreementUri` and`download` set to `true`), and adds the path segments to the request.
   ```
   GET /api/artifacts/{id}/data/John/3
   ```
6. The consumer connector sends the additional path segments specified in the request to
   `GET /api/artifacts/{id}/data` to the provider connector as the payload of the
   `ArtifactRequestMessage`. After receiving the message, the provider connector fetches the data
   from the backend system after appending the specified path segments to the pre-defined URL.
   ```
   http://example-backend.com/John/3
   ```
