---
layout: default
title: Kubernetes
nav_order: 4
description: ""
permalink: /Deployment/Kubernetes
parent: Deployment
---

# Kubernetes
{: .fs-9 }

Example and description for deploying the Dataspace Connector in Kubernetes.
{: .fs-6 .fw-300 }

---

In addition to the instructions below, the IDSA community provides a free 
[GitHub repository](https://github.com/International-Data-Spaces-Association/IDS-Deployment-Examples) 
with sample deployments. These include not only the Dataspace Connector or a deployment together 
with ConfigManager and GUI, but also some with other IDS components. The goal is to provide an easy 
entry into the whole IDS ecosystem. Feel free to have a look at the files or contribute with your 
own examples.

## PostgreSQL Deployment

The Dataspace Connector uses an internal H2 database per default. When deploying the Dataspace 
Connector in a Kubernetes cluster, this will lead to inconsistencies as soon as there are multiple 
connector replicas. Therefore, an external database should be used.

Execute the 3 following commands in the given order in the root directory of the project to start a 
PostgreSQL database that the Dataspace Connector can use:

```commandline
kubectl create -f postgres-configmap.yaml
kubectl apply -f postgres-deployment.yaml
kubectl expose -f postgres-service.yaml 
```

Now, a PostgreSQL instance is running and accessible by other services in the cluster using the 
`service name` (*postgres*) and the `service port` (*5432*).

## Connector Deployment

### Secret

Kubernetes uses secrets for storing sensitive data like passwords or certificates. The Dataspace 
Connector uses an IDS certificate, an SSL certificate, and a truststore, all of which should not be 
exposed to the outside or be easily accessible. Thus, these files should be stored in a secret, 
which can then be mounted to a specified directory of the Dataspace Connector pods.

If you want to use an external configuration (`config.json`), you can add it to the secret as well 
or create a second secret containing the configuration.

---

**Note:**
The example deployment expects the configuration to be in the same secret as the certificates.

---

To create a secret, put all files it should contain in a directory and execute the following 
command:

```commandline
kubectl create secret generic dataspace-connector-certs --from-file=path/to/certs/directory
```

If you create a second secret, be sure to define and mount that in `deployment.yaml` and, if 
necessary, change the property `CONFIGURATION_PATH` to point to the config file in the specified 
mounted directory. If you don't want to use an external configuration file, delete the property 
`CONFIGURATION_PATH` from `deployment.yaml`.

### Deployment

A deployment tells Kubernetes how to set up an application. That includes e.g. the image to use, 
environment variables, and resource requirements (memory, CPU). The `deployment.yaml` gives an 
example on how to configure the Dataspace Connector deployment.

##### Configuration

Settings from `application.properties`: At `env`, all properties presented in or being available 
for Spring's `application.properties` can be added or overridden. In this example, the Dataspace 
Connector uses a PostgreSQL database. If you want to use another database, the corresponding 
database values can be changed here. You can also set the path to the configuration file in case of 
supplying an external configuration file or specify the SSL certificate to use.

Image: The registry and specific images to be used for the deployment can be configured at 
`image`. In the example, a local docker registry running on port 5000 is used. For the example, the
[local registry](https://docs.docker.com/registry/deploying/) has to be running and contain the 
Dataspace Connector image. Alternatively, you can change the registry and image name in the 
deployment file or omit the registry to use a locally built image.

If you want to use a private registry that requires credentials, first create a docker-registry 
secret:

```commandline
kubectl create secret docker-registry registry-credentials --docker-server=[registry-server] 
    --docker-username=[username] --docker-password=[password] --docker-email=[email]
```

You can then tell Kubernetes to use this secret when pulling the image by adding the following lines 
to `deployment.yaml` at `spec` with `imagePullSecrets` being on the same level as `containers` and 
`volumes`:

```yaml
imagePullSecrets:
  - name: registry-credentials
```

Mounted Directory: In the example, the secret containing the key- and truststores as well as 
the configuration file is mounted to the pod at `/connector-certs`. For the connector to find the 
certificates, the paths in the `config.json` have to be set to `/connector-certs/[certificate name]`. 
Alternatively, you can change the mount path of the secret at `volumeMounts`.

##### Starting the deployment

To start the deployment, execute the following command:

```commandline
kubectl apply -f deployment.yaml
```

### Service

A service is an abstraction for a set of pods and defines how the set can be accessed. In a service, 
you can define e.g. what port your application should expose and under what name other services can 
reach it. The `service.yaml` gives an example on how to define a service for the Dataspace 
Connector. There are different types of services. The service type defaults to `ClusterIP` if not 
specified, as is the case here, meaning that it can be accessed by other services in the cluster 
using the service's `name` and `port`. To make the service accessible from outside the cluster,
either choose a different 
[service type](https://kubernetes.io/docs/concepts/services-networking/service/) 
or create an [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/).

To start the service as type `LoadBalancer` (reachable from inside and outside the cluster), execute

```commandline
kubectl expose -f service.yaml --type=LoadBalancer
```

Afterwards, you can find the IP of the Master node by executing

```commandline
kubectl cluster-info
```

and the NodePort the service is running on by executing

```commandline
kubectl describe service dataspace-connector
```

With this IP and port you can now reach the connector using e.g. cURL or an HTTP client.

---

**Note:**
This example was tested using Minikube. Depending on the Kubernetes distribution you 
use, `kubectl` might have to be replaced with another command.
* When using OpenShift, replace `kubectl` with `oc`.
* When using MicroK8s, replace `kubectl` with `microk8s kubectl`
* When using Minikube, replace `kubectl` with `minikube kubectl --`
