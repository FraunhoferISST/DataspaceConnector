<h1 align="center">
  <br>
    <img alt="Logo" width="200" src="docs/assets/images/dsc_logo.png"/>
  <br>
      Dataspace Connector
  <br>
</h1>


<p align="center">
  <a href="mailto:info@dataspace-connector.de">Contact</a> •
  <a href="#contributing">Contribute</a> •
  <a href="https://international-data-spaces-association.github.io/DataspaceConnector/">Docs</a> •
  <a href="https://github.com/International-Data-Spaces-Association/DataspaceConnector/issues">Issues</a> •
  <a href="#license">License</a>
</p>


The Dataspace Connector is an implementation of an IDS connector component following the
[IDS Reference Architecture Model](https://www.internationaldataspaces.org/wp-content/uploads/2019/03/IDS-Reference-Architecture-Model-3.0.pdf).
It integrates the [IDS Information Model](https://github.com/International-Data-Spaces-Association/InformationModel)
and uses the [IDS Messaging Services](https://github.com/International-Data-Spaces-Association/IDS-Messaging-Services)
for IDS functionalities and message handling.
The core component in this repository provides a REST API for loading, updating, and deleting
resources with local or remote data enriched by its metadata. It supports IDS conform message
handling with other IDS connectors and components and implements usage control for selected IDS
usage policy patterns.

***

<h3 align="center" >
  <a href="https://international-data-spaces-association.github.io/DataspaceConnector/">
    D O C U M E N T A T I O N
  </a>
</h3>

***

## Quick Start

We provide Docker images. These can be found
[here](https://github.com/International-Data-Spaces-Association/DataspaceConnector/pkgs/container/dataspace-connector).

For an easy deployment, make sure that you have [Docker](https://docs.docker.com/get-docker/)
installed. Then, execute the following command:

```commandline
docker run -p 8080:8080 --name connector ghcr.io/international-data-spaces-association/dataspace-connector:latest
```

If everything worked fine, the connector is available at
[https://localhost:8080/](https://localhost:8080/). The API can be accessed at
[https://localhost:8080/api](https://localhost:8080/api). The Swagger UI can be found at
[https://localhost:8080/api/docs](https://localhost:8080/api/docs).
Next, please take a look at our
[communication guide](https://international-data-spaces-association.github.io/DataspaceConnector/CommunicationGuide).

For a more detailed or advanced Docker or Kubernetes deployment, as well as a full setup with the
Connector and its GUI, see [here](https://github.com/International-Data-Spaces-Association/IDS-Deployment-Examples/tree/main/dataspace-connector).

If you want to build and run locally, follow [these](https://international-data-spaces-association.github.io/DataspaceConnector/GettingStarted#quick-start) steps.

## Contributing

You are very welcome to contribute to this project when you find a bug, want to suggest an
improvement, or have an idea for a useful feature. Please find a set of guidelines at the
[CONTRIBUTING.md](CONTRIBUTING.md) and the [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## Developers

This is an ongoing project of the [Data Business](https://www.isst.fraunhofer.de/en/business-units/data-economy.html)
department of the [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html).

The core development is driven by
* [Heinrich Pettenpohl](https://github.com/HeinrichPet), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html), Project Manager
* [Julia Pampus](https://github.com/juliapampus), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html), Lead Developer
* [Brian-Frederik Jahnke](https://github.com/brianjahnke), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Ronja Quensel](https://github.com/ronjaquensel), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)

with significant contributions, comments, and support by (in alphabetical order):
* [Erik van den Akker](https://github.com/vdakker), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Fabian Bruckner](https://github.com/fabianbruckner), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Gökhan Kahriman](https://github.com/goekhanKahriman), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Haydar Qarawlus](https://github.com/hqarawlus), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Johannes Pieperbeck](https://github.com/jpieperbeck), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Omar Luiz Barreto Silva](https://github.com/ob-silva), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [René Brinkhege](https://github.com/renebrinkhege), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Steffen Biehs](https://github.com/steffen-biehs), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)
* [Tim Berthold](https://github.com/tmberthold), [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html)

## License
Copyright © 2020 Fraunhofer ISST. This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) for details.
