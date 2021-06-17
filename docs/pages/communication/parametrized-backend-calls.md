---
layout: default
title: Parametrized Backend Calls
nav_order: 3
description: ""
permalink: /CommunicationGuide/ParametrizedBackendCalls
parent: Communication Guide
---

# Parametrized backend calls
{: .fs-9 }

See how to use query parameters, path segments and headers for the backend request when requesting an artifact.
{: .fs-6 .fw-300 }

---

The Dataspace Connector supports the usage of custom headers, query parameters and path segments when fetching data
from backend systems. Therefore, it is possible for a data consumer to specify them when requesting data from a data
provider.

## Provider

The `Resource` class has a field `endpointDocumentation` of type URI. Thus, when registering a resource, a data provider
can add a link pointing to the API documentation for the referenced backend. In that documentation the path segments,
query parameters and headers expected or supported by the backend API should be described, if any.

When defining the URL of an HTTP backend, the provider can either give just the base URL

	  http://example-backend.com

or already add path segments or query parameters, if they should always be present:

	  http://example-backend.com?key=value

	  http://example-backend.com/some/path/segments?key=value

When a request is made by a consumer specifying additional parameters or path segments, they will be appended to the
already existing ones.

## Consumer

After requesting a resource's metadata, a data consumer can follow the link to the documentation, decide which
headers, query parameters and path segments they have to and want to use and then specify them when requesting the data.

To be able to request the data, the consumer first has to negotiate a contract with the provider for the desired
artifact (using `POST /api/ids/contract`). The consumer connector then also queries the artifact's and the corresponding
resource's metadata and stores them in its database. The response contains a link to the agreement made, which
references the ID of the newly created artifact. Using this artifact's ID, the consumer can request the corresponding
data using `GET /api/artifacts/{id}/data` (with the URI of the previously created agreement as the `agreementUri` and
`download` set to `true`). To this request, any additional path segments, query parameters and headers can be added,
so that the request may look as follows:

      GET /api/artifacts/{id}/data/additional/path
      GET /api/artifacts/{id}/data?param1=value1&param2=value2
      GET /api/artifacts/{id}/data/additional/path?param1=value1&param2=value2

The consumer connector sends any additional query parameters, path segments and headers specified in the request to
`GET /api/artifacts/{id}/data` to the provider connector in the ArtifactRequestMessage.

## Examples

### Query parameters

#### Scenario

A data provider wants to share data from a REST API that uses query parameters. They don't want to create an additional
artifact for each possible combination of query parameters, but instead offer the API's data using one single
artifact and letting the data consumer decide on the query parameters they want to use.

#### Steps

1. Provider defines the base URL of the REST API as the URL in the artifact:
   `http://example-backend.com`
2. Provider provides a link to the REST API documentation in the field `endpointDocumentation` of the resource's
   metadata.
    ```
        Query parameters according to API documentation:
            required: name (any string)
            optional: number (any positive integer)
    ```
3. Consumer follows the link to the REST API documentation after requesting the resource's metadata
   (using `POST /api/ids/description`).
4. Consumer negotiates a contract with the provider for the desired artifact (using `POST /api/ids/contract`).
    * The response contains a link to the agreement made, which references the ID of the newly created artifact.
5. Consumer uses the artifact's ID to request the corresponding data, using `GET /api/artifacts/{id}/data` (with the
   URI of the previously created agreement as the `agreementUri` and `download` set to `true`), and adds the query
   parameters to the request.
    1) only the required parameter
        ```
            GET /api/artifacts/{id}/data?name=John
        ```
    2) the required and the optional parameter
        ```
            GET /api/artifacts/{id}/data?name=John&number=3
        ```
6. The consumer connector sends the additional query parameters specified in the request to
   `GET /api/artifacts/{id}/data` to the provider connector in the ArtifactRequestMessage. After receiving the message,
   the provider connector fetches the data from the backend system after appending the specified query parameters to
   the URL.
    1) `http://example-backend.com?name=John`
    2) `http://example-backend.com?name=John&number=3`

### Path segments

#### Scenario

A data provider wants to share data from a REST API that supports different path segments. They don't want to create an
additional artifact for each possible path segment or each possible combination of segments, but instead offer the
API's data using one single artifact and letting the data consumer decide which path segment(s) they want to use.

#### Steps

1. Provider defines the URL of the REST API as the URL in the resource artifact:
   `http://example-backend.com/`
2. Provider provides a link to the REST API documentation in the field `endpointDocumentation` of the resource's
   metadata.
    ```
       Path segments according to API documentation:
            required: name (any string), number (any positive integer)
    ```
3. Consumer follows the link to the REST API documentation after requesting the resource's metadata
   (using `POST /api/ids/description`).
4. Consumer negotiates a contract with the provider for the desired artifact (using `POST /api/ids/contract`).
    * The response contains a link to the agreement made, which references the ID of the newly created artifact.
5. Consumer uses the artifact's ID to request the corresponding data, using `GET /api/artifacts/{id}/data` (with the
   URI of the previously created agreement as the `agreementUri` and `download` set to `true`), and adds the path
   segments to the request.
    ```
        GET /api/artifacts/{id}/data/John/3
    ```
6. The consumer connector sends the additional path segments specified in the request to
   `GET /api/artifacts/{id}/data` to the provider connector in the ArtifactRequestMessage. After receiving the message,
   the provider connector fetches the data from the backend system after appending the specified path segments to
   the URL.
    ```
    http://example-backend.com/John/3
    ```
