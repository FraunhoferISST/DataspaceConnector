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

In this section, the registration of resources during the startup is described.

## Configuration

The bootstrapping feature is disabled by default. To enable it, change the `bootstrap.enabled=false`
value in the `application.properties` to `true`. Then, follow the steps below.

## Registering Elements at the Connector

During the startup of the Dataspace Connector, the bootstrapping path that is specified in
`application.properties` will be scanned for `*.jsonld` and `bootstrap.properties` files. The search
includes all subdirectories found in the base path.

Each `jsonld`-file contains the JSON-LD representation of an IDS catalog. These representations will
be loaded and registered at the connector. This includes all elements that are part of the catalog,
except requested resources. The IDs used for the IDS catalog are used to prevent duplicates among
multiple startups.

The `bootstrap.properties` files contain additional data that is not present in the IDS Information
Model representations. This includes credentials, URLs, and values for artifacts as well as
information on which resources should be registered at IDS Brokers. If multiple
`bootstrap.properties` files are found, all of them will be loaded and merged. If there are
collisions, these will be logged and the first found value will be kept.

An example for a valid catalog that can be used for bootstrapping is shown below. Corresponding
sample files are provided at `test/resources/bootstrap`.

```json
{
  "@context" : {
    "ids" : "https://w3id.org/idsa/core/",
    "idsc" : "https://w3id.org/idsa/code/"
  },
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

An example for a valid `bootstrap.properties` file can be found below.

```properties
#artifact.accessUrl.https\://w3id.org/idsa/autogen/artifact/d5b1cd4e-2a5a-47c2-86c5-003c6a11ce69=
#artifact.username.https\://w3id.org/idsa/autogen/artifact/d5b1cd4e-2a5a-47c2-86c5-003c6a11ce69=
#artifact.password.https\://w3id.org/idsa/autogen/artifact/d5b1cd4e-2a5a-47c2-86c5-003c6a11ce69=
artifact.value.https\://w3id.org/idsa/autogen/artifact/d5b1cd4e-2a5a-47c2-86c5-003c6a11ce69=Example Value
broker.register.https\://w3id.org/idsa/autogen/resource/d5b1cd4e-2a5a-47c2-86c5-003c6a11ce69=https://broker.ids.isst.fraunhofer.de/infrastructure
```

## Registering Elements at the IDS Broker
Each resource being registered during the bootstrapping process can be registered at an IDS Broker.
It is possible to register different resources at different brokers, but each resource can only be
registered at one broker once. The registration of a resource at a broker implicitly registers the
connector itself at the broker.

In order to register a resource at the broker, an entry with the following structure must be placed
in a `bootstrap.properties` file:

```properties
broker.register.<RESOURCE_ID>=<BROKER_ENDPOINT>
```

An example for the first resource of the example IDS catalog can be found below.

```properties
broker.register.https\://w3id.org/idsa/autogen/resource/e32c5397-f71c-47b5-a384-c9c4cf568117=https://broker.ids.isst.fraunhofer.de/infrastructure
```
