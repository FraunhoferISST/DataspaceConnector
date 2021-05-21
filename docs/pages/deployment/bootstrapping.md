---
layout: default
title: Bootstrapping
nav_order: 1
description: ""
permalink: /Deployment/Bootstrapping
parent: Deployment
---

# Bootstrapping
{: .fs-9 }

Load resources during startup and register them at a broker.
{: .fs-6 .fw-300 }

---

In this section the registration of resources during the startup will be described.

### Registering Elements at the Connector

During the startup of the connector, the bootstrapping path, which is specified in `application.properties`
will be scanned for `*.jsonld` and `bootstrap.properties` files. The search includes all subdirectories
found in the base path.

Each `jsonld`-file contains the JSON-LD representation of an IDS catalog. These representations will be
loaded and registered at the connector. This includes all elements which are part of the catalog, except
requested resources. The ids used for the IDS catalog are used to prevent duplicates among multiple
startups.

An example for a valid catalog which can be used for bootstrapping can be found below.

```json
{
  "@type": "ids:ResourceCatalog",
  "@id": "https://w3id.org/idsa/autogen/resourceCatalog/12548e90-c094-424a-a207-6c736f817492",
  "ids:offeredResource": [
    {
      "@type": "ids:Resource",
      "@id": "https://w3id.org/idsa/autogen/resource/a7d8c819-c7f7-49a1-9e0e-759fbd077a97",
      "ids:language": [
        {
          "properties": null,
          "@id": "idsc:EN"
        }
      ],
      "ids:contentType": null,
      "ids:variant": null,
      "ids:description": [
        {
          "@value": "This is an example resource",
          "@language": "EN"
        }
      ],
      "ids:created": null,
      "ids:version": "1",
      "ids:title": [
        {
          "@value": "Example Resource",
          "@language": "EN"
        }
      ],
      "ids:publisher": "https://example.com",
      "ids:sovereign": "https://example.com",
      "ids:theme": null,
      "ids:representation": [
        {
          "@type": "ids:Representation",
          "@id": "https://w3id.org/idsa/autogen/representation/842693b1-80d1-4d24-8923-b82ce3937cf6",
          "ids:instance": [
            {
              "@type": "ids:Artifact",
              "@id": "https://w3id.org/idsa/autogen/artifact/5c96b6f0-a698-4329-9f15-4913bf4e86f5",
              "ids:fileName": "exampleFile.xml",
              "ids:duration": 12551,
              "ids:creationDate": "2016-10-03T15:10:40.000Z",
              "ids:byteSize": 4214,
              "ids:checkSum": "120EA8A25E5D487BF68B5F7096440019"
            },
            {
              "@type": "ids:Artifact",
              "@id": "https://w3id.org/idsa/autogen/artifact/570d3831-0186-42ac-b323-d60fc207d71a",
              "ids:fileName": "anotherFile.xml",
              "ids:duration": 7552,
              "ids:creationDate": "2016-12-03T15:10:40.000Z",
              "ids:byteSize": 6441,
              "ids:checkSum": "1E0EA8A25E5D487BF68A5F7026440079"
            }
          ],
          "ids:language": {
            "properties": null,
            "@id": "idsc:EN"
          },
          "ids:created": "2016-10-03T15:10:40.000Z",
          "ids:mediaType": {
            "@type": "ids:IANAMediaType",
            "@id": "https://w3id.org/idsa/autogen/iANAMediaType/eb710d80-4a75-48ab-b34a-734ab9009af0",
            "ids:filenameExtension": "xml"
          },
          "ids:representationStandard": "https://example.com",
          "ids:shapesGraph": "https://example.com",
          "ids:modified": "2017-10-03T15:10:40.000Z"
        }
      ],
      "ids:resourceEndpoint": [
        {
          "@type": "ids:ConnectorEndpoint",
          "@id": "https://w3id.org/idsa/autogen/connectorEndpoint/c02d9dd2-9ab6-41d6-80f1-1844d64ba6ff",
          "ids:path": null,
          "ids:endpointInformation": null,
          "ids:accessURL": "https://example.com"
        }
      ],
      "ids:temporalResolution": null,
      "ids:temporalCoverage": null,
      "ids:accrualPeriodicity": null,
      "ids:defaultRepresentation": null,
      "ids:shapesGraph": null,
      "ids:contentStandard": null,
      "ids:standardLicense": "https://example.com",
      "ids:spatialCoverage": null,
      "ids:customLicense": null,
      "ids:resourcePart": null,
      "ids:sample": null,
      "ids:contentPart": null,
      "ids:contractOffer": [
        {
          "@type": "ids:ContractOffer",
          "@id": "https://w3id.org/idsa/autogen/contractOffer/57c89dfa-1dc6-430c-a6ba-9b8d9af2d8b1",
          "ids:provider": "https://example.com",
          "ids:permission": null,
          "ids:obligation": null,
          "ids:prohibition": null,
          "ids:contractDate": "2016-10-03T15:10:40.000Z",
          "ids:contractAnnex": null,
          "ids:contractStart": "2017-10-03T15:10:40.000Z",
          "ids:contractEnd": "2018-10-03T15:10:40.000Z",
          "ids:consumer": "https://example.com",
          "ids:contractDocument": null
        }
      ],
      "ids:keyword": [
        {
          "@value": "EXAMPLE",
          "@language": "EN"
        },
        {
          "@value": "DATA",
          "@language": "EN"
        }
      ],
      "ids:modified": null
    },
    {
      "@type": "ids:Resource",
      "@id": "https://w3id.org/idsa/autogen/resource/5ef66d83-d69f-4c3d-9b75-ae0f569576e8",
      "ids:language": [
        {
          "properties": null,
          "@id": "idsc:EN"
        }
      ],
      "ids:contentType": null,
      "ids:variant": null,
      "ids:description": [
        {
          "@value": "This is an example resource",
          "@language": "EN"
        }
      ],
      "ids:created": null,
      "ids:version": "1",
      "ids:title": [
        {
          "@value": "Second Example Resource",
          "@language": "EN"
        }
      ],
      "ids:publisher": "https://example.com",
      "ids:sovereign": "https://example.com",
      "ids:theme": null,
      "ids:representation": [
        {
          "@type": "ids:Representation",
          "@id": "https://w3id.org/idsa/autogen/representation/d141e149-e7d2-48cb-b5fc-aafbd191f2f6",
          "ids:instance": [
            {
              "@type": "ids:Artifact",
              "@id": "https://w3id.org/idsa/autogen/artifact/405aff0f-a154-4ecc-8e59-c3c942ad4ba0",
              "ids:fileName": "exampleFile.pdf",
              "ids:duration": 127899,
              "ids:creationDate": "2016-10-03T15:10:40.000Z",
              "ids:byteSize": 64167,
              "ids:checkSum": "120EA8A25E5D487BF6AB2684DD440019"
            },
            {
              "@type": "ids:Artifact",
              "@id": "https://w3id.org/idsa/autogen/artifact/6b9c9add-03f0-44da-b185-4f24c7f13e27",
              "ids:fileName": "anotherFile.pdf",
              "ids:duration": 21677,
              "ids:creationDate": "2016-10-03T15:10:40.000Z",
              "ids:byteSize": 98783,
              "ids:checkSum": "1E0EA8A25E2421BF68A5F7026440079"
            }
          ],
          "ids:language": {
            "properties": null,
            "@id": "idsc:EN"
          },
          "ids:created": "2016-10-03T15:10:40.000Z",
          "ids:mediaType": {
            "@type": "ids:IANAMediaType",
            "@id": "https://w3id.org/idsa/autogen/iANAMediaType/ce22d1c0-e546-4066-a0e8-4a4e7620f883",
            "ids:filenameExtension": "pdf"
          },
          "ids:representationStandard": "https://example.com",
          "ids:shapesGraph": "https://example.com",
          "ids:modified": "2016-10-03T15:10:40.000Z"
        }
      ],
      "ids:resourceEndpoint": [
        {
          "@type": "ids:ConnectorEndpoint",
          "@id": "https://w3id.org/idsa/autogen/connectorEndpoint/755d877e-94aa-49e0-90d8-bec2a5186b48",
          "ids:path": null,
          "ids:endpointInformation": null,
          "ids:accessURL": "https://example.com"
        }
      ],
      "ids:temporalResolution": null,
      "ids:temporalCoverage": null,
      "ids:accrualPeriodicity": null,
      "ids:defaultRepresentation": null,
      "ids:shapesGraph": null,
      "ids:contentStandard": null,
      "ids:standardLicense": "https://example.com",
      "ids:spatialCoverage": null,
      "ids:customLicense": null,
      "ids:resourcePart": null,
      "ids:sample": null,
      "ids:contentPart": null,
      "ids:contractOffer": [
        {
          "@type": "ids:ContractOffer",
          "@id": "https://w3id.org/idsa/autogen/contractOffer/d2232122-776c-4a5d-b2a9-c3c69fe30e83",
          "ids:provider": "https://example.com",
          "ids:permission": null,
          "ids:obligation": null,
          "ids:prohibition": null,
          "ids:contractDate": "2016-10-03T15:10:40.000Z",
          "ids:contractAnnex": null,
          "ids:contractStart": "2016-10-03T15:10:40.000Z",
          "ids:contractEnd": "2016-10-03T15:10:40.000Z",
          "ids:consumer": "https://example.com",
          "ids:contractDocument": null
        }
      ],
      "ids:keyword": [
        {
          "@value": "EXAMPLE",
          "@language": "EN"
        },
        {
          "@value": "DOCUMENT",
          "@language": "EN"
        }
      ],
      "ids:modified": null
    }
  ],
  "ids:requestedResource": [
  ]
}
```

Since the IDS representation of catalogs does not contain information about the `remoteURL` for resources,
which is required for the registration of resources at the connector, this data has to be provided in the
`bootstrap.properties` file(s). For each resource, an entry with the key `resource.remoteUrl.<RESOURCE_ID>`,
which defines the `remoteURL` for this resource, must be present.

An example, which defines the `remoteURL` for the first resource of the example IDS catalog,
can be found below.

```properties
resource.remoteUrl.https\://w3id.org/idsa/autogen/resource/a7d8c819-c7f7-49a1-9e0e-759fbd077a97=https://example.com
```

### Registering Elements at the Broker
Each resource, which has been registered during the bootstrapping, can be registered at a broker, too.
It is possible to register different resources at different brokers, but each resource can only be registered
at one broker. The registration of a resource at a broker implicitly registers the connector itself at the
broker.

In order to register a resource at a broker, an entry with the following structure must be placed in a
`bootstrap.properties` file:

```properties
broker.register.<RESOURCE_ID>=<BROKER_ENDPOINT>
```

An example for the first resource of the example IDS catalog can be found below.

```properties
broker.register.https\://w3id.org/idsa/autogen/resource/a7d8c819-c7f7-49a1-9e0e-759fbd077a97=https://broker.ids.isst.fraunhofer.de/infrastructure
```
