# Dataspace Connector

`Java`, `Maven`, `Spring Boot`, `Rest`, `OpenAPI`, `Docker`, `JSON(-LD)`

**Contact**: [info@dataspace-connector.de](mailto:info@dataspace-connector.de)
| **Issues**: Feel free to report issues [here](https://github.com/FraunhoferISST/DataspaceConnector/issues) 
or write an [email](mailto:info@dataspace-connector.de).

The Dataspace Connector integrates the 
[IDS Information Model](https://github.com/International-Data-Spaces-Association/InformationModel) 
and uses the [IDS Framework](https://github.com/FraunhoferISST/IDS-Connector-Framework) 
for IDS functionalities and message handling. It provides a REST API for loading, updating, and 
deleting resources with data and its metadata, persisted in a local database. Next to the internal 
database, external REST endpoints may be connected as data sources. The Dataspace Connector 
supports IDS conform message handling with other IDS connectors and IDS brokers and implements 
usage control for eight IDS usage policy patterns. 

Basic information about the IDS reference architecture model can be found 
[here](https://www.internationaldataspaces.org/wp-content/uploads/2019/03/IDS-Reference-Architecture-Model-3.0.pdf).

---

This is an ongoing project of the [Data Economy](https://www.isst.fraunhofer.de/en/business-units/data-economy.html) 
business unit of the [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html). You are very welcome 
to contribute to this project when you find a bug, want to suggest an improvement, or have an idea 
for a useful feature. Please find a set of guidelines at the [CONTRIBUTING.md](CONTRIBUTING.md).
---

## Content

- [IDS Components](#ids-components) 
- [Home](https://github.com/FraunhoferISST/DataspaceConnector/wiki)  
    - [IDS-ready](https://github.com/FraunhoferISST/DataspaceConnector/wiki#ids-ready)
    - [Supported Features](https://github.com/FraunhoferISST/DataspaceConnector/wiki#supported-features)  
        - [Core Functionality](https://github.com/FraunhoferISST/DataspaceConnector/wiki#core-functionality)
        - [IDS Functionality](https://github.com/FraunhoferISST/DataspaceConnector/wiki#ids-functionality)
- [Getting started](https://github.com/FraunhoferISST/DataspaceConnector/wiki/getting-started)  
    - [Java Setup](https://github.com/FraunhoferISST/DataspaceConnector/wiki/getting-started#java-setup)  
    - [Docker Setup](https://github.com/FraunhoferISST/DataspaceConnector/wiki/getting-started#docker-setup)  
- [Development](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development)  
    - [Configurations](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#configurations)  
        - [Proxy](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#proxy)  
        - [Authentication](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#authentication)  
        - [Database](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#database)
    - [Deployment](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#deployment)  
        - [Maven Build](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#maven-build)  
        - [Docker Setup](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#docker-setup)  
        - [Run Tests](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#run-tests)  
        - [Backend API](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#backend-api)  
    - [Example Setup](https://github.com/FraunhoferISST/DataspaceConnector/wiki/development#example-setup)  
- [License](#license)

Further information about logging, policies, software documentation etc. can be found in the wiki as well. 
An overview is presented [here](https://github.com/FraunhoferISST/DataspaceConnector/wiki).

## IDS Components

| Library | Version | License | Owner | Contact |
| ------- | ------- | ------- | ----- | ------- |
| [IDS Information Model Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel/) | 4.0.0 | Apache 2.0 | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [IDS Information Model Serializer Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel-serializer/) | 4.0.0 | Apache 2.0 | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [IDS Framework](https://github.com/FraunhoferISST/IDS-Connector-Framework) | 4.0.1 | Apache 2.0 | Fraunhofer ISST | [Tim Berthold](mailto:tim.berthold@isst.fraunhofer.de) |

The [ConfigManager](https://github.com/FraunhoferISST/IDS-ConfigurationManager) and its 
[GUI](https://github.com/fkie/ids-configmanager-ui) aim to facilitate the configuration of the 
Dataspace Connector and further IDS connector implementations. Both projects are also open source.

| Component | Version | License | Owner | Contact |
| --------- | ------- | ------- | ----- | ------- |
| [IDS Broker](https://broker.ids.isst.fraunhofer.de/) | 4.0.0 | open core | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [DAPS](https://daps.aisec.fraunhofer.de/) | 2.0 | not open source | Fraunhofer AISEC | [Gerd Brost](mailto:gerd.brost@aisec.fraunhofer.de) |


## License
Copyright © 2020 Fraunhofer ISST. This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) for details.
