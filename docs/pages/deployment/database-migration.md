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

With version 7.0.0, the Dataspace Connector can migrate its database to the current schema.
This is possible starting from version 5.0.0. If enabled, the migration is performed by the
connector on start-up. This guide will explain step by step how to use the migration feature.

__The migration is written for *PostgreSQL* and has not been tested with other databases!__

__It is highly recommended that you do a back-up of your database before starting the migration!__

__Configurations are not migrated.__

## How-to

In the `application.properties`:
```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=<your-last-connector-version>
spring.jpa.hibernate.ddl-auto=update
```

1. __Make a backup of the database, if not already done!__
2. Set `spring.flyway.enabled` to `true`.
3. Set `spring.flyway.baseline-on-migrate` to true, if your database is not empty before
   the migration.
4. Set `spring.flyway.baseline-version` to the last connector version used with the database
   (omit the `v`, so e.g. `5.0.0`).
5. Set `spring.jpa.hibernate.ddl-auto` to `update`.
6. Start the connector.

The connector will then perform the migration on start-up.

The migration should remain enabled after it has been performed once, as new connector versions
might introduce changes to the database which require another migration.

### Helm

The migration is disabled in the `Helm` charts by default, but can be enabled by updating the
following section in the `values.yaml` file:

```yaml
  flyway:
    SPRING_FLYWAY_ENABLED: "true"
    SPRING_FLYWAY_BASELINE-ON-MIGRATE: "true"
    SPRING_FLYWAY_BASELINE-VERSION: "5.0.0"
    SPRING_JPA_HIBERNATE_DDL-AUTO: "update"
```

Make sure to set `SPRING_FLYWAY_BASELINE-VERSION` to the last DSC version you used with the
database to migrate.

## Manual configurations

Under certain conditions, some manual configurations are required after the migration has been
performed. You can find details on what actions are required in the sections below.

### Data sources with type DATABASE

Data sources of type `DATABASE` are deleted during the migration. Before that, references to these
data sources are removed from the `endpoint` table so that the database is still in a consistent
state.

#### Actions to take

Re-create all previously persisted data sources and link them to the respective endpoints.

#### Explanation

The model for data sources of type `DATABASE` has changed, as additional fields have been added
for this type. These fields describe connection properties of a database and hence cannot be
inferred or populated with default values. As existing data sources of type `DATABASE` would
be invalid after the migration and thus would lead to errors if used, they are deleted.

### Routes referencing connector endpoints

Routes that were previously linked to a connector endpoint are now missing either their
`start` or their `end` endpoint.

#### Actions to take

If the connector endpoint was the end of the route, the route now has to be
[linked to an artifact](#how-to-link-routes-and-artifacts) (*output*) in order to be deployed
as a Camel route.

#### Explanation

The `ConnectorEndpoint` entity has been removed from the data model in v7.0.0. When Camel routes
are created by the connector, the information previously held by the connector endpoint is now
inferred from the context.

### Route output

All links between routes and artifacts are removed during the migration.

#### Actions to take

Links between routes and artifacts have to be re-created by
[adding or updating an artifact with a route reference](#how-to-link-routes-and-artifacts).

#### Explanation

Routes and artifacts can be linked to each other to show which route provides the data for a
specific artifact. Previously, routes could be linked to any number of artifacts (*one-to-many*),
but from v7.0.0 on, one route can only reference a single artifact as its output (*one-to-one*).

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
