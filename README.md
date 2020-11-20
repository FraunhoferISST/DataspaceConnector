# Dataspace Connector

**Contact**: [info@dataspace-connector.de](mailto:info@dataspace-connector.de)
| **Issues**: Feel free to report issues [here](https://github.com/FraunhoferISST/DataspaceConnector/issues) or write an [email](mailto:info@dataspace-connector.de).

This is an IDS Connector using the specifications of the [IDS Information Model](https://github.com/International-Data-Spaces-Association/InformationModel) with integration of the [IDS Framework](https://gitlab.cc-asp.fraunhofer.de/fhg-isst-ids/ids-framework) for connector configuration and message handling.
It provides a REST API for loading, updating, and deleting simple data resources with data and its metadata, persisted in a local H2 database. Next to the internal database, external HTTP REST endpoints as data sources can be connected as well.
The connector supports IDS conform message handling with other IDS connectors and IDS brokers and implements usage control for eight IDS usage policy patterns. 

**This repository has a `develop` branch in addition to the `master` branch. The idea is to always merge other branches into the `develop` branch (as SNAPSHOT version) and to push the changes from there into the `master` only for releases. This way, the `develop` branch is always up to date, with the risk of small issues, while the `master` only contains official releases.**

Basic information about the International Data Spaces reference architecture model can be found [here](https://www.internationaldataspaces.org/wp-content/uploads/2019/03/IDS-Reference-Architecture-Model-3.0.pdf).

**This is an ongoing project of the [Data Economy](https://www.isst.fraunhofer.de/en/business-units/data-economy.html) business unit of the [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html). You are very welcome to contribute to this project when you find a bug, want to suggest an improvement, or have an idea for a useful feature. Please find a set of guidelines at the [CONTRIBUTING.md](CONTRIBUTING.md).**

**This repository has a `develop` branch in addition to the `master` branch. The idea is to always merge other branches into the `develop` branch (as SNAPSHOT version) and to push the changes from there into the `master` only for releases. This way, the `develop` branch is always up to date, with the risk of small issues, while the `master` only contains official releases.**

## Content
 
- [Features](#features)  
    - [Technologies](#technologies)  
    - [IDS Components](#ids-components)  
- [Getting started](#getting-started)  
    - [Java Setup](#java-setup)  
    - [Docker Setup](#docker-setup)  
- [Example Setup](#example-setup)  
- [Development](#development)  
    - [Configurations](#configurations)  
        - [Proxy](#proxy)  
        - [Authentication](#authentication)  
        - [Database](#database)
    - [Deployment](#deployment)  
        - [Maven Build](#maven-build)  
        - [Docker Setup](#docker-setup)  
        - [Run Tests](#run-tests)  
    - [Backend API](#backend-api)  
- [License](#license)

## Features

This is a list of currently implemented features, which is continuously updated.

*  Settings for TLS, proxy and Spring Boot basic authentication for backend endpoints
*  Use valid IDS certificate and request DAT from DAPS
*  Data resource registration (CRUD metadata) with internal H2 database
*  Backend data handling internal (CRUD data) with internal H2 database
*  Backend data handling external with example Rest Api (external spring boot application with H2 database)
*  IDS message handling with other IDS connectors (as data provider and data consumer): description request/response, artifact request/response, rejection message
*  Read IDS response messages: save requested data & metadata in internal database
*  IDS message handling with the IDS broker (IDS lab): available/update, unavailable, query 
*  Usage control with ODRL policies following the IDS policy language specifications
*  Possibility to add multiple representations (different backend connections) to a resource

### Technologies

`Java`, `Maven`, `Spring Boot`, `Rest`, `OpenAPI`, `Swagger`, `SLF4J`, `Docker`, `JSON(-LD)`

### IDS Components

| Library/Component | Version | License | Owner | Contact |
| ------ | ------ | ------ | ------ | ------ |
| [IDS Information Model Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel/) | 4.0.0 | Apache 2.0 | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [IDS Information Model Serializer Library](https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/de/fraunhofer/iais/eis/ids/infomodel-serializer/) | 4.0.0 | Apache 2.0 | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [IDS Framework](https://gitlab.cc-asp.fraunhofer.de/fhg-isst-ids/ids-framework) | 3.2.3 | Apache 2.0 | Fraunhofer ISST | [Steffen Biehs](mailto:steffen.biehs@isst.fraunhofer.de) |
| [IDS Broker](https://broker.ids.isst.fraunhofer.de/) | 4.0.0 | not open source | Fraunhofer IAIS | [Sebastian Bader](mailto:sebastian.bader@iais.fraunhofer.de) |
| [DAPS](https://daps.aisec.fraunhofer.de/) | 2.0 | not open source | Fraunhofer AISEC | [Gerd Brost](mailto:gerd.brost@aisec.fraunhofer.de) |


## Getting started

At first, clone the repository: `git clone https://github.com/FraunhoferISST/DataspaceConnector.git`.

If you want to deploy the connector yourself, follow the instructions of the [Development Section](#development). If you do not want to build the connector yourself and just want to see how two connectors communicate, take a look at the **two test setups placed at the corresponding [release](https://github.com/FraunhoferISST/DataspaceConnector/releases)**. 
Both test setups provide a connector as a data provider and one as a data consumer.

### Java Setup

Extract the provided `java-setup.zip` file. Make sure you have Java 11 installed and both `.jar` files inside their own folder. 
The data provider will be running at https://localhost:8080 and the consumer at https://localhost:8081.

For requesting data from the provider, open the Swagger UI of the consumer (https://localhost:8081/admin/api with `admin` + `password`) and send a request as shown below. 
Due to the missing requested resource, the self-description of the provider is returned in response. To request a specific resource, it has to be created in the provider first.
A more detailed explanation can be found at [Hands-on IDS Communication](https://github.com/FraunhoferISST/Dataspace-Connector/wiki/Hands-on-IDS-Communication).

![Data Request from Consumer to Provider](images/example.PNG)

### Docker Setup 

Extract the provided `docker-setup.zip` file. Make sure you have Docker Compose installed and run `docker-compose build --no-cache` and then `docker-compose up` inside the extracted folder. 
In doing so, the provided `.jar` files will be built up as Docker Images and started as a data provider running at http://localhost:8080/ and a data consumer running at http://localhost:8081/.

For requesting data from the provider, please remind that all applications are running inside isolated docker containers. So don't request e.g. http://localhost:8080/api/ids/data but http://provider:8080/api/ids/data.


## Example Setup

An instance of the Dataspace Connector v2.0 is currently available in the IDS Lab at https://simpleconnector.ids.isst.fraunhofer.de/. 
It can only be reached from inside a VPN network. To get your IP address unblocked, please contact [Julia Pampus](mailto:julia.pampus@isst.fraunhofer.de).
* The connector self-description is available at https://simpleconnector.ids.isst.fraunhofer.de/ (GET).
* The **open endpoint for IDS communication** is https://simpleconnector.ids.isst.fraunhofer.de/api/ids/data (POST).
* The backend API (available at `/admin/api`) and its endpoints are only accessible to users with credentials. 

**Testing:**
1. When requesting the connector's self-description, the included catalog gives information about available resources. The resource id (e.g. https://w3id.org/idsa/autogen/dataResource/[UUID]) is essential for requesting an artifact or description.
2. The open endpoint at `/api/ids/data` expects an ArtifactRequestMessage with a known resource id as RequestedArtifact (for requesting data) or a DescriptionRequestMessage with a known resource id as RequestedElement (for requesting metadata). 
    * If this parameter is not known to the connector, you will receive a RejectionMessage as response. 
    * If the RequestedElement is missing at a DescriptionRequestMessage, you will receive the connector's self-description.
    * When sending a simple RequestMessage, you will receive an echo response containing your message body.
3. The running connector offers two data resources. One contains a simple string, the other one a base64 encoded image.


## Development

If you want to setup the connector application yourself, follow the instructions below. If you encounter any problems, please have a look at the [FAQ](https://github.com/FraunhoferISST/Dataspace-Connector/wiki/Frequently-Asked-Questions). 

### Configurations

The resource folder `conf` provides three important files that are loaded at application start:

* `keystore-localhost.p12`: The provided keystore, on the one hand, is used as IDS certificate that is loaded by the IDS Framework for requesting a valid Dynamic Attribute Token (DAT) from the Dynamic Attribute Provisioning Service (DAPS). 
Each message to IDS participant needs to be signed with a valid DAT. On the other hand, it is used as SSL certificate for TLS encryption.
* `truststore.p12`: The truststore is used by the IDS Framework for any Https communication. It ensures the connection to trusted addresses.
* `config.json`: The configuration is used to set important properties for IDS message handling.

**Step 1**: When starting the application, the `config.json` will be scanned for important connector information, e.g. its UUID, its address, contact information, or proxy settings. 
Please keep this file up to date to your own connector settings. In case you are using the demo cert, you don't need to change anything except the [**proxy settings**](#proxy). 

**If you want to connect to a running connector or any other system running at `https`, keep in mind that you need to add the keystore to your truststore. 
Otherwise the communication will fail. For now, with the provided truststore, the Dataspace Connector will accept its own localhost certificate, public certificates, and any IDS keystore that was provided by the Fraunhofer AISEC.**

_If you are not familiar with the IDS Information Model, the `MainController` class provides an endpoint `GET /example/configuration` to print a filled in Java object as JSON-LD. Adapt this to your needs, take the received string and place it in the `config.json`._

**Step 2**: In the provided `config.json`, the `ids:connectorDeployMode` is set to `idsc:TEST_DEPLOYMENT`. This allows to use the `keystore-localhost.p12` as an IDS certificate. 
For testing purpose, the existing cert can be used, as on application start, the IDS Framework will not get a valid DAT from the DAPS and for received messages, the sent DAT will not be checked. 

To turn on the DAT checking, you need to set the `ids:connectorDeployMode` to `idsc:PRODUCTIVE_DEPLOYMENT`. For getting a trusted certificate, contact [Gerd Brost](mailto:gerd.brost@aisec.fraunhofer.de). 
Add the keystore with the IDS certificate inside to the `resources/conf` and change the filename at `ids:keyStore` accordingly. 

**The TEST_DEPLOYMENT and accepting a demo cert is for testing purposes only! This mode is a security risk and cannot ensure that the connector is talking to a verified IDS participant. Furthermore, messages from the Dataspace Connector without a valid IDS certificate will not be accepted by other connectors.** 

**Step 3 (optional)**: The `application.properties` specifies database, SSL, spring security, open API, and DAPS configurations. 
    To define on which port the connector should be running, change `server.port={PORT}`. 
    If you want to add your own SSL certificate, check the corresponding path. 
    
_As the provided certificate only supports the application running at `localhost`, you may replace this with your IDS keystore, if you want to host the connector in a productive environment._

#### Proxy

For outgoing requests, the connector needs information about an existing system proxy that needs to be set in the `src/main/resources/conf/config.json`.

```
"ids:connectorProxy" : [ {
    "@type" : "ids:Proxy",
    "@id" : "https://w3id.org/idsa/autogen/proxy/548dc73a-ccfb-4039-9569-4b8e219b90bc",
    "ids:proxyAuthentication" : {
      "@type" : "ids:BasicAuthentication",
      "@id" : "https://w3id.org/idsa/autogen/basicAuthentication/47e3cd59-d351-4f5b-99fc-561c94bad5e1"
    },
    "ids:proxyURI" : {
      "@id" : "http://host:port"
    },
    "ids:noProxy" : [ {
      "@id" : "https://localhost:8080/"
    }, {
      "@id" : "http://localhost:8080/"
    } ]
  } ]
```

Check if your system is running behind a proxy. If this is the case, specify the `ids:proxyURI` and change `ids:noProxy` if necessary. Otherwise, delete the key `ids:connectorProxy` and its values.


#### Authentication
The application uses HTTP Basic Authentication. Each endpoint behind `/admin/**` needs a user authentication. 

Have a look at the blocked endpoints in the `ConfigurationAdapter` class to add or change endpoints yourself.
In case you don't want to provide authentication for your backend maintenance, feel free to remove the corresponding lines.

If you want to change the default credentials, go to `application.properties`. The properties are located at `spring.security.user.name=admin` and `spring.security.user.name=password`.

#### Database

The Dataspace Connector uses Spring Data JPA to set up the database and manage interactions with it. Spring Data JPA 
supports many well-known relational databases out of the box. Thus, the internal H2 can be replaced by e.g. MySQL, 
PostgreSQL, or Oracle databases with minimal effort.

To use another database for the Connector, follow these steps: [Database Configuration](https://github.com/FraunhoferISST/DataspaceConnector/wiki/Database-configuration)

### Deployment

In the following, the deployment with Maven and Docker will be explained.

#### Maven Build    

If you want to build and run locally, ensure that Java 11 is installed. Then, follow these steps:

1.  Execute `cd dataspace-connector` and `mvn clean package`.
2.  The connector can be started by running the Spring Boot Application. Therefore, navigate to `/target` and run `java -jar dataspace-connector-{VERSION}.jar`.
3.  If everything worked fine, the connector is available at https://localhost:8080/. By default, it is running with an h2 database.

_After successfully building the project, the Javadocs as a static website can be found at `/target/apidocs`. Open the `index.html` in a browser of your choice._

#### Docker Setup

If you want to deploy in docker and build the maven project with the Dockerfile, follow these steps:

**Option 1: Build and run Docker image**
1. Navigate to `dataspace-connector`. To build the image, run `docker build -t <IMAGE_NAME:TAG> .` (e.g. `docker build -t dataspaceconnector .`). 
2. For running your image as a container, follow [these](https://docs.docker.com/get-started/part2/) instructions: `docker run --publish 8080:8080 --detach --name bb <IMAGE_NAME:TAG>`

**Option 2: Using Docker Compose**
1. The `docker-compose.yml` sets up the connector application and a PostgreSQL database. If necessary, make your changes in the `connector.env` and `postgres.env`. Please find more details about setting up different databases [here](https://github.com/FraunhoferISST/DataspaceConnector/wiki/Database-Configuration).
2. If you are starting the application for the very first time, change `spring.jpa.hibernate.ddl-auto=update` in the `application.properties` to `spring.jpa.hibernate.ddl-auto=create`. 
3. For starting the application, run `docker-compose up`. Have a look at the `docker-compose.yaml` and make your own configurations if necessary.
4. For any further container starts, reset the setting of Step 2 to `update`. **Otherwise, changes in the database will be lost and overwritten.** Rebuild the image by running `docker-compose build --no-cache` and then follow Step 3.

If you just want to run a built jar file (with an H2 database) inside a docker image, have a look at the `Dockerfile` inside the [`docker-setup.zip`](https://github.com/FraunhoferISST/DataspaceConnector/releases).

#### Run Tests

Tests will be executed automatically when running Maven commands `package`, `verify`, `install`, `site`, or `deploy`. An overview of the implemented test classes is placed at [Supported Test Classes](https://github.com/FraunhoferISST/Dataspace-Connector/wiki/Supported-Test-Classes).

To run tests manually, execute the following commands in the root directory of the project:

Run all tests
```
mvn test
```
Run specific test class:
```
mvn test -Dtest=[full class name]
mvn test -Dtest=de.fraunhofer.isst.dataspaceconnector.integrationtest.SelfDescriptionTest
```
        
Run a specific test case (single method)
```
mvn test -Dtest=[full class name]#[method name]
mvn test -Dtest=de.fraunhofer.isst.dataspaceconnector.integrationtest.SelfDescriptionTest#getSelfDescription_noResources
```


### Backend API

The OpenApi documentation can be viewed at https://localhost:8080/admin/api. 
The JSON representation is available at https://localhost:8080/v3/api-docs. 
The .yaml file can be downloaded at https://localhost:8080/v3/api-docs.yaml.

**OpenApi**

The connector provides several endpoints for resource database handling and IDS messaging. Details on how to interact with them can be found at [Hands-on IDS Communication](https://github.com/FraunhoferISST/Dataspace-Connector/wiki/Hands-on-IDS-Communication).

*  `Connector: Selfservice` provides information about the running connector
*  `Connector: Resource Handling` provides endpoints for local data resource management (register, delete, update data/metadata, and load metadata)
*  `Backend: Resource Data Handling` provides endpoints for local data management (register, delete, update data, and load data)
*  `Connector: IDS Connector Communication` provides endpoints for requesting artifact (data) and descriptions (metadata) from an external connector (ArtifactRequestMessage, DescriptionRequestMessage)
*  `Connector: IDS Broker Communication` provides endpoints for IDS broker messages (ConnectorAvailableMessage, ConnectorUnavailableMessage, ConnectorInactiveMessage, ConnectorUpdateMessage, QueryMessage)

Next to the ones accessible by using the Swagger UI, the connector, respectively the IDS Framework, provides an IDS endpoint for handling incoming data requests at `/api/ids/data`.

**Database**

The data resources are persisted in an H2 database.

*  Local datasource: `/target/db/resources`
*  Console path: https://localhost:8080/admin/h2


## License
Copyright © 2020 Fraunhofer ISST. This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) for details.
