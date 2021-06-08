---
layout: default
title: Bootstrapping
nav_order: 3
description: ""
permalink: /Deployment/Bootstrapping
parent: Deployment
---

# Bootstrapping
{: .fs-9 }

Load resources during startup and register them at an IDS Broker.
{: .fs-6 .fw-300 }

---

In this section, the registration of resources during the startup will be described.

### Registering Elements at the Connector

During the startup of the Dataspace Connector, the bootstrapping path, which is specified in 
`application.properties`will be scanned for `*.jsonld` and `bootstrap.properties` files. The search 
includes all subdirectories found in the base path.

Each `jsonld`-file contains the JSON-LD representation of an IDS catalog. These representations will 
be loaded and registered at the connector. This includes all elements which are part of the catalog, 
except requested resources. The IDs used for the IDS catalog are used to prevent duplicates among 
multiple startups.

An example for a valid catalog that can be used for bootstrapping can be found below. Corresponding 
sample files are provided at `test/resources/bootstrap`.

```json
{
  "@type": "ids:ResourceCatalog",
  "@id": "https://w3id.org/idsa/autogen/resourceCatalog/4a1fbd8c-8f23-4cc0-871d-9d26596b00c9",
  "ids:offeredResource": [
    {
      "@type": "ids:Resource",
      "@id": "https://w3id.org/idsa/autogen/resource/e32c5397-f71c-47b5-a384-c9c4cf568117",
      "ids:language": [
        {
          "@id": "idsc:EN"
        }
      ],
      "ids:description": [
        {
          "@value": "This is an example resource",
          "@language": "EN"
        }
      ],
      "ids:version": "3",
      "ids:title": [
        {
          "@value": "Example Another Resource",
          "@language": "EN"
        }
      ],
      "ids:publisher": "https://example.com",
      "ids:sovereign": "https://example.com",
      "ids:representation": [
        {
          "@type": "ids:Representation",
          "@id": "https://w3id.org/idsa/autogen/representation/f74ab1ad-3a1b-4508-aada-4859dcfa7349",
          "ids:instance": [
            {
              "@type": "ids:Artifact",
              "@id": "https://w3id.org/idsa/autogen/artifact/d5b1cd4e-2a5a-47c2-86c5-003c6a11ce69",
              "ids:fileName": "exampleFile.xml",
              "ids:duration": 12512351,
              "ids:creationDate": "2016-10-03T15:10:40.000Z",
              "ids:byteSize": 425314,
              "ids:checkSum": "120ECEF25E5D487BF68B5F709644D219"
            }
          ],
          "ids:language": {
            "properties": null,
            "@id": "idsc:EN"
          },
          "ids:created": "2016-10-03T15:10:40.000Z",
          "ids:mediaType": {
            "@type": "ids:IANAMediaType",
            "@id": "https://w3id.org/idsa/autogen/iANAMediaType/07e5f54e-4e85-4df7-94d3-a7e8b225f1cb",
            "ids:filenameExtension": "xml"
          },
          "ids:representationStandard": "https://example.com",
          "ids:shapesGraph": "https://example.com",
          "ids:modified": "2016-10-03T15:10:40.000Z"
        }
      ],
      "ids:resourceEndpoint": [
        {
          "@type": "ids:ConnectorEndpoint",
          "@id": "https://w3id.org/idsa/autogen/connectorEndpoint/4458078e-c2f2-4d9f-afbe-54e9daa4c1b8",
          "ids:accessURL": "https://example.com"
        }
      ],
      "ids:standardLicense": "https://example.com",
      "ids:contractOffer": [
        {
          "@type": "ids:ContractOffer",
          "@id": "https://w3id.org/idsa/autogen/contractOffer/1d982c8a-c5ae-4c19-9a26-6815e9540fc8",
          "ids:permission": [
            {
              "@type": "ids:Permission",
              "@id": "https://w3id.org/idsa/autogen/permission/a3c12cd0-5022-484d-8fb0-0676351de2da",
              "ids:description": [
                {
                  "@value": "provide-access",
                  "@type": "http://www.w3.org/2001/XMLSchema#string"
                }
              ],
              "ids:title": [
                {
                  "@value": "Allow Data Usage",
                  "@type": "http://www.w3.org/2001/XMLSchema#string"
                }
              ],
              "ids:action": [
                {
                  "@id": "idsc:USE"
                }
              ]
            }
          ]
        }
      ],
      "ids:keyword": [
        {
          "@value": "EXAMPLE",
          "@language": "EN"
        }
      ]
    }
  ]
}

```

Since the IDS representation of catalogs does not contain information about the `remoteURL` for 
resources, which is required for the registration of resources at the connector, this data has to be 
provided in the `bootstrap.properties` file(s). For each resource, an entry with the key 
`resource.remoteUrl.<RESOURCE_ID>` that defines the `remoteURL` for this resource must be present.

An example that defines the `remoteURL` for the first resource of the example IDS catalog, can be 
found below.

```properties
resource.remoteUrl.https\://w3id.org/idsa/autogen/resource/a7d8c819-c7f7-49a1-9e0e-759fbd077a97=https://example.com
```

### Registering Elements at the Broker
Each resource that has been registered during the bootstrapping process can be registered at an 
IDS Broker. It is possible to register different resources at different brokers, but each resource 
can only be registered at one broker once. The registration of a resource at a broker implicitly 
registers the connector itself at the broker.

In order to register a resource at a broker, an entry with the following structure must be placed in 
a `bootstrap.properties` file:

```properties
broker.register.<RESOURCE_ID>=<BROKER_ENDPOINT>
```

An example for the first resource of the example IDS catalog can be found below.

```properties
broker.register.https\://w3id.org/idsa/autogen/resource/e32c5397-f71c-47b5-a384-c9c4cf568117=https://broker.ids.isst.fraunhofer.de/infrastructure
```
