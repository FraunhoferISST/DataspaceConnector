<h1 align="center">
  <br>
  <a href="https://dataspace-connector.de/dsc_logo.svg"><img src="https://dataspace-connector.de/dsc_logo.svg" alt="Dataspace Connector Logo" width="200"></a>
  <br>
      Dataspace Connector
  <br>
</h1>


<p align="center">
  <a href="mailto:info@dataspace-connector.de">Contact</a> •
  <a href="#con">Contribute</a> •
  <a href="https://github.com/FraunhoferISST/DataspaceConnector/issues">Issues</a> •
  <a href="#license">License</a> •
  <a href="https://github.com/FraunhoferISST/DataspaceConnector/wiki">Wiki</a>
</p>


The Dataspace Connector is an implementation of an IDS connector component following the 
[IDS Reference Architecture Model](https://www.internationaldataspaces.org/wp-content/uploads/2019/03/IDS-Reference-Architecture-Model-3.0.pdf). 
It integrates the [IDS Information Model](https://github.com/International-Data-Spaces-Association/InformationModel) 
and uses the [IDS Connector Framework](https://github.com/FraunhoferISST/IDS-Connector-Framework) 
for IDS functionalities and message handling. It provides a REST API for loading, updating, and 
deleting resources with local or remote data enriched by its metadata. The Dataspace Connector 
supports IDS conform message handling with other IDS connectors and IDS brokers and implements 
usage control for eight IDS usage policy patterns. 

## Content
- [Wiki](https://github.com/FraunhoferISST/DataspaceConnector/wiki)   
    - [Database Configuration](https://github.com/FraunhoferISST/DataspaceConnector/wiki/database-configuration)
    - [Deployment](https://github.com/FraunhoferISST/DataspaceConnector/wiki/deployment)  
    - [Examples](https://github.com/FraunhoferISST/DataspaceConnector/wiki/examples)
    - [Frequently Asked Questions](https://github.com/FraunhoferISST/DataspaceConnector/wiki/faq)   
    - [Getting started](https://github.com/FraunhoferISST/DataspaceConnector/wiki/getting-started)  
    - [IDS Communication Guide](https://github.com/FraunhoferISST/DataspaceConnector/wiki/ids-communication-guide)
    - [IDS Connector Architecture](https://github.com/FraunhoferISST/DataspaceConnector/wiki/ids-connector-architecture)
    - [Kubernetes](https://github.com/International-Data-Spaces-Association/DataspaceConnector/wiki/Kubernetes)
    - [Logging](https://github.com/FraunhoferISST/DataspaceConnector/wiki/logging)
    - [Parametrized Backend Calls](https://github.com/International-Data-Spaces-Association/DataspaceConnector/wiki/Parametrized-Backend-Calls) 
    - [Roadmap](https://github.com/FraunhoferISST/DataspaceConnector/wiki/roadmap)
    - [Software Documentation](https://github.com/FraunhoferISST/DataspaceConnector/wiki/software-documentation)
    - [Software Tests](https://github.com/FraunhoferISST/DataspaceConnector/wiki/software-tests)
    - [Usage Control](https://github.com/FraunhoferISST/DataspaceConnector/wiki/usage-control)
    - [Using Camel](https://github.com/International-Data-Spaces-Association/DataspaceConnector/wiki/Using-Camel)
- [Quick Start](#quick-start)
- [IDS Components](#ids-components)
- [Contributing](#contributing)
- [Developers](#developers)
- [License](#license)

A project overview and short descriptions of each wiki section are presented 
[here](https://github.com/FraunhoferISST/DataspaceConnector/wiki).

## Quick Start

If you want to build and run locally, ensure that at least Java 11 is installed. Then, follow these steps:

1.  Clone this repository.
2.  Execute `cd DataspaceConnector` and `./mvnw clean package`.
3.  Navigate to `/target` and run `java -jar dataspace-connector-{VERSION}.jar`.
4.  If everything worked fine, the connector is available at https://localhost:8080/. The API can 
be accessed at https://localhost:8080/admin/api.

For more details, see [here](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development).
If you do not want to deploy the application yourself, have a look at 
[how to use the test setups](https://github.com/FraunhoferISST/DataspaceConnector/wiki/getting-started).

## IDS Components

The [ConfigManager](https://github.com/FraunhoferISST/IDS-ConfigurationManager) and its 
[GUI](https://github.com/fkie/ids-configmanager-ui) aim to facilitate the configuration of the 
Dataspace Connector and further IDS connector implementations. Both projects are also open source.

| Library/ Component | License | Owner | Contact |
| ------- | ------- | ----- | ------- |
| [IDS Information Model Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel/) | Apache 2.0 | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [IDS Information Model Serializer Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel-serializer/) | Apache 2.0 | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [IDS Framework](https://github.com/FraunhoferISST/IDS-Connector-Framework) | Apache 2.0 | Fraunhofer ISST | [Tim Berthold](mailto:tim.berthold@isst.fraunhofer.de) |
| [IDS Broker](https://broker.ids.isst.fraunhofer.de/) | open core | Fraunhofer IAIS | [Matthias Böckmann](mailto:matthias.boeckmann@iais.fraunhofer.de) |
| [DAPS](https://daps.aisec.fraunhofer.de/) | not open source | Fraunhofer AISEC | [Gerd Brost](mailto:gerd.brost@aisec.fraunhofer.de) |

## Contributing

You are very welcome to contribute to this project when you find a bug, want to suggest an 
improvement, or have an idea for a useful feature. Please find a set of guidelines at the 
[CONTRIBUTING.md](CONTRIBUTING.md) and the [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## Developers

This is an ongoing project of the [Data Economy](https://www.isst.fraunhofer.de/en/business-units/data-economy.html) 
business unit of the [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html). 

The core development is driven by
* [Heinrich Pettenpohl](https://github.com/HeinrichPet), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Julia Pampus](https://github.com/juliapampus), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Brian-Frederik Jahnke](https://github.com/brianjahnke), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Ronja Quensel](https://github.com/ronjaquensel), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)

with significant contributions, comments, and support by (in alphabetical order):
* [Haydar Qarawlus](https://github.com/hqarawlus), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Johannes Pieperbeck](https://github.com/jpieperbeck), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [René Brinkhege](https://github.com/renebrinkhege), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Steffen Biehs](https://github.com/steffen-biehs), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)

## License
Copyright © 2020 Fraunhofer ISST. This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) for details.
