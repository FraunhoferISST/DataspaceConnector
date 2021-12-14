---
layout: default
title: Database Migration
nav_order: 1
description: ""
permalink: /Deployment/DatabaseMigration
parent: Deployment
---

# Database Migration
{: .fs-9 }

On this page, you can find some more details on how to migrate the connector's database to the
version 7 schema.
{: .fs-6 .fw-300 }

---

With version 7.0.0, the Dataspace Connector can migrate its database to the current schema
(almost) without losing any data. This is possible starting from version 5.0.0. If enabled,
the migration is performed by the connector on start-up. This guide will explain step by
step how to use the migration feature.

__Note, that the migration is written for *PostgreSQL* and will not work with other databases!__

## How-to

The only thing to configure for using the migration feature are 3 new properties in the
`application.properties` file.

```properties
### DB Migration
spring.flyway.enabled=false
spring.flyway.baseline-on-migrate=false
spring.flyway.baseline-version=5.0.0
```

As long as `spring.flyway.enabled` is set to false, no migration will be performed on the
database when the connector starts. So the first step is changing this value to `true`.
If your database is not empty before starting the migration - which it will most likely not be -
set the property `spring.flyway.baseline-on-migrate` to `true` as well.
The property `spring.flyway.baseline-version` should point to the last connector version used
with the database to migrate. So for example, if you are using version 6.2.0 of the connector
before the migration, you have to set this property to `6.2.0` when starting the connector in
the new version 7.0.0.

Also, the property `spring.jpa.hibernate.ddl-auto` __must__ be set to `validate`. If it is set
to `create`, `create-drop` or `update`, Spring will re-create or modify the database before any
migration script is executed. The property is set to `validate` by default, but should be checked
if an old `env`-file is reused.

After the properties are configured, you only need to start the connector in version 7 and it
will automatically perform the migration. If you restart the connector afterwards, you can set
`spring.flyway.enabled` to `false` again. If you don't change the property back, `Flyway` will
not try to perform the migration again, as it realizes the schema is up-to-date, but it will
still initialize and load the migration scripts.

## Important notes

The introduction to this guide states, that the database can be migrated __almost__ without
losing any data. Some information cannot be migrated to the new schema and is therefore
deleted during the migration.

### Data sources with type DATABASE

There are two types of datasources: `REST` and `DATABASE`. The model for data sources of type
`REST` does not change between the 6.x.x versions and v7.0.0, so they are still available after
the migration. This is different for data sources of type `DATABASE`, as additional fields have
been added for this type:

```java
/**
 * JDBC URL of the database.
 */
private String url;

/**
 * Name of the driver class to use for connecting to the database.
 */
private String driverClassName;
```

These fields describe connection properties of a database and hence cannot be inferred.
As existing data sources of type `DATABASE` would be invalid after the migration and thus would
lead to errors if used, they are deleted. Before that, references to these data sources are
removed from the `endpoint` table so that the database is still in a consistent state.

__That means, that all data sources of type `DATABASE` will have to be re-added after the migration
and linked to the respective endpoints again!__

### Routes referencing connector endpoints

The `ConnectorEndpoint` entity has been removed from the data model in v7.0.0. When Camel routes
are created by the connector, the information previously held by the connector endpoint is
inferred from the context. As a consequence, routes that were previously linked to a connector
endpoint are now missing either their `start` or their `end` endpoint.

If the connector endpoint was the start of the route, no action has to be taken. These routes
can be specified to dispatch data when data is requested via `GET /api/artifacts/{id}/data` and
thus use the respective artifact's data as their input. Therefore, defining a start endpoint for
these routes is not required anymore.

If the connector endpoint was the end of the route, the route will not be executable as is.
It now has to be [linked to an artifact](#how-to-link-routes-and-artifacts) (*output*).
The route will not run on a timer as before, but is run whenever the linked artifact's data is
requested. It will therefore return the data instead of pushing it to an endpoint of the
connector.

### Route output

Routes and artifacts can be linked to each other to show which route provides the data for a
specific artifact. Previously, routes could be linked to any number of artifacts (*one-to-many*),
but from v7.0.0 on, one route can only reference a single artifact as its output (*one-to-one*).
Therefore, the relation table for routes and artifacts is removed during the migration and
previously existing links between the two entities are deleted. These links have to be re-created
after the migration. In v7.0.0, this can not be done using an API endpoint under `/api/routes`, but
is only achieved when
[adding or updating an artifact with a route reference](#how-to-link-routes-and-artifacts).

---

### How to link routes and artifacts

In order to link an artifact to a route, the route's ID (URI, not just UUID) has to be specified
as the artifact's `accessUrl` when creating or updating an artifact:

`POST /api/artifacts` or `PUT /api/artifacts/<uuid>`

```json
{
  "accessUrl": "<connector-url>/api/routes/<route-uuid>"
}
```

As only one artifact can reference a route at a time, adding/updating an artifact with a route
URL will fail if there is already another artifact referencing the same route.
