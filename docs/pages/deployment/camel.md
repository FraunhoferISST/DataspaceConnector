---
layout: default
title: Camel
nav_order: 4
description: ""
permalink: /Deployment/Camel
parent: Deployment
---

# Customizing Apache Camel Routes
{: .fs-9 }

Here, you can find instructions on how to customize Apache Camel within the Dataspace Connector.
{: .fs-6 .fw-300 }

---

The Dataspace Connector uses Camel routes to execute the logic of message handlers - the components
responsible for processing incoming IDS messages. The logic for each message handler is split into
small steps, where each step is executed by its own Camel processor. Thus, the message handling
process becomes modular. Therefore, custom steps, like e.g. sending a confirmed agreement to another
service, can easily be integrated into this process. This documentation describes how to achieve
this.

## Adding a custom processor

### Creating the processor

To add custom logic to a route, you have to create your own implementation of a Camel processor.
This is down by implementing the `org.apache.camel.Processor` interface and overriding the `process`
method that is of type void and takes an `org.apache.camel.Exchange` as parameter. An exchange is
used in Camel routes to transfer information between endpoints. It has a `getIn()` method that
returns the message held by the exchange. Calling `getBody()` on that message returns the current
body, `setBody(Object object)` sets a new body. In the `process` method, you can now execute custom
logic based on the body and modify it, if desired.

The processor implementations in the package `io.dataspaceconnector.camel.processor` can be used for
reference.

---

**Note:** All processors already defined use `io.dataspaceconnector.service.message.handler.dto.Request` and
`io.dataspaceconnector.service.message.handler.dto.Response` as the message bodies. So if you set any other object as
the body in your processor's `process` method, the following processors in the route will fail!

---

### Using the processor in a route

To be able to reference a processor in a route, it first has to be added as a Spring Bean. Simply
annotate your processor class with `@Component("yourProcessorsName")` to achieve this. Afterwards,
you can call it anywhere in a route using:

```xml
<process ref="yourProcessorsName"/>
```


## Modifying routes without recompilation

As the Dataspace Connector uses the Spring XML DSL instead of the Java DSL for defining Camel
routes, routes can be modified without recompiling the code. All routes used by the connector reside
in the directory `/src/main/resources/camel-routes` and are loaded from there at application start.
The directory from which the routes are loaded can be changed in the `application.properties`, so
that it is possible to load the routes from external directories residing somewhere in the file
system.

Default setting:
```properties
camel.xml-routes.directory=classpath:camel-routes
```

Example for using an external directory:
```properties
camel.xml-routes.directory=/some/directory
```

When using an external directory, the Camel routes can be modified and reloaded by just restarting
the connector, instead of recompiling the whole project.

---

**Note**: The Dataspace Connector requires all routes present in the `camel-routes` directory to run
correctly. If any of the routes or any steps in the routes are missing, this may lead to unforeseen
errors! So if you want to load routes from a custom directory, the best way to do so is to copy all
files from the `camel-routes` directory to the desired directory and then modify them there.
