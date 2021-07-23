# Changelog
All notable changes to this project will be documented in this file.

## [6.0.0] - 2021-07-20

### Added
- Provide REST endpoint for full-text search at the IDS Broker: `/ids/search`.
- Check if the issuer connector of an artifact request does correspond to the signed consumer of the
  transfer contract.
- Integrate Camel-Spring-Boot version 3.10.0.
- Integrate [DSC Camel Instance repository](https://github.com/International-Data-Spaces-Association/DSC-Camel-Instance).
  * Provide REST endpoints for adding and removing Camel routes and Spring beans at runtime.
- Send `ArtifactRequest` and `ArtifactResponse` messages to the Clearing House.
- Allow artifacts pointing to backend systems to be created with both BasicAuth and API key
  authentication.
- Integrate IDSCPv2 for IDS communication.
  * Add property `idscp2.enabled` for enabling and disabling IDSCPv2 server. Is disabled by default.
  * Add properties for configuring keystore and truststore for IDSCPv2.
  * When enabling IDSCPv2, a valid IDS certificate is required!
- Implement subscription transfer pattern.
  * Add user profile for apps/services with access to subscription REST endpoints.
  * Allow subscriptions for offered & requested resources, representations, and artifacts via REST
    endpoints.
  * Create `PUT /notify` endpoint to manually notify subscribers (ids & non-ids).
  * Automatically notify subscribers on entity updates.
  * Create REST endpoints for sending (un-)subscriptions via ids messages.
- Integrate [IDS ConfigManager repository](https://github.com/International-Data-Spaces-Association/IDS-ConfigurationManager).
  * Extend data model and REST API by entities: auth, broker, configuration, datasource, endpoint,
    keystore, proxy, route, and truststore.
  * Add Camel error handler for propagating errors in routes.
- Persist connector configuration to database.
  * Load configuration from database.
  * Choose active configuration from list of configurations.

### Changed
- Replace IDS Connector Framework v5.0.4 by IDS Messaging Services v2.0.1.
- Edit response codes and response content for the following endpoints: `/ids/connector/unavailable`,
  `/ids/connector/update`, `/ids/resource/unavailable`, `/ids/resource/update`, `/ids/query`.
- Move implementation for sending IDS query, connector, and resource messages to
  `GlobalMessageService`.
- Handle DAT retrieving errors in `PRODUCTIVE_DEPLOYMENT` with status code 500 and a corresponding
  message.
- Artifact PUT `/api/data` changed response code from Ok (200) to NoContent (204).
- Change naming of the resource's license attribute from `licence` to `license`.
- Change `AbstractEntity` to `Entity` and `NamedEntity`.
- Refactor message handlers using Camel routes.
- Increase postgres version to 42.2.23.
- Increase jackson version to 2.12.4.
- Increase equalsverifier from 3.6.1 to 3.7.0.
- Increase spotbugs from 4.2.3 to 4.3.0.
- Increase spring version from 2.5.2 to 2.5.3.

## [5.2.1] - 2021-07-02

### Changed
- Increase spring-boot version to 2.5.2.
- Increase checkstyle version to 8.44.
- Increase pmd version to 6.36.0.

### Fixed
- Make bootstrapping feature optional. It can be enabled in the `application.properties`.

## [5.2.0] - 2021-06-23

### Added
- Add `BootstrapConfiguration`.
  * Allow registering ids catalogs, offered resources, representations, artifacts, and contract
    offers during start up.
  * Allow registering offered resources as part of the catalogs to brokers.
- Add `CatalogTemplate` and matching mapping/build functions.
- Add a method to `AbstractIdsBuilder` that allows to create elements with a custom base URI.
- Add `bootstrap.path` to `application.properties` to define the base path where bootstrapping data
  can be found.

### Changed
- Change `ConnectorService` to use the connector's ID from `config.json` when
  `getAllCatalogsWithOfferedResources` is called.

### Fixed
- Fixed missing IDS context in `/api/examples/policy`.
- Disable autocommit on database transactions.
- Remove encoding from optional path segment in `HttpService`.

### Changed
- Increase IDS Framework version to 5.0.4.
- Update default Infomodel version to 4.0.10.
- Increase postgres version to 42.2.22.

## [5.1.2] - 2021-06-14

### Fixed
- Fixed deletion of artifact data before the set time.
- Fixed tags with different descriptions in openapi schema.
- Fixed missing paging information in openapi schema.

### Changed
- Increase postgresql version to 42.2.21.
- Increase spring-boot version to 2.5.1.

## [5.1.1] - 2021-06-09

### Fixed
- Add missing hateoas information in openapi schema.

## [5.1.0] - 2021-06-07

### Added
- Add telemetry collection via Jaeger.

### Changed
- Replace deprecated JPA calls (`getOne` -> `getById`).
- Increase length restriction for URIs in database columns to 2048.
- Increase modelmapper version to 2.4.4.
- Increase equalsverifier version to 3.6.1.
- Increase spring-openApi-security version to 1.5.9.
- Increase spring-openapi-ui version to 1.5.9.
- Increase maven-javadoc-plugin version to 3.3.0.
- Increase spring-boot version to 2.5.0.
- Increase checkstyle version to 8.43.
- Increase pmd version to 6.35.0.
- Increase pitest version from 1.6.6 to 1.6.7.

### Fixed
- Representations have now only one self-link.
- Set Basic Auth Header on (provider) backend calls.
- Ignore empty extension `/**` on `GET **/artifacts/{id}/data/**`.
- `GET **/artifacts/{id}/data` and `POST **/artifacts/{id}/data` will now return the same output.

## [5.0.2] - 2021-05-25

### Changed
- Make the Clearing House url setting optional in `application.properties`.

### Fixed
- Persist URIs as strings in database.

## [5.0.1] - 2021-05-19

### Changed
- H2 does not persist database to files.
- Change additional list mapping for received attribute list with a single item.
- Disable unused rolling file appender in `log4j2.xml`.

## [5.0.0] - 2021-05-17

### Added
- Partially support of HATEOAS.
- Add pagination for REST calls on resources.
- Integration and configuration of Jaeger for using open telemetry.
- Set default application name to `Dataspace Connector` in `application.properties`.
- Add custom spring banner.
- Add separate controller methods for each IDS message type.
- Add global exception handlers for `ResourceNotFoundException`, `JsonProcessingException`, and any
  `RuntimeException`.
- Add possibility to disable http tracer in `application.properties`.
- Add possibility to restrict depth of returned IDS information on `DescriptionRequest`.
  * Change IDS self-description to returning only a list of catalogs instead of their whole content.
  * Add possibility to send `DescriptionRequestMessages` for other elements than resources.
- Add remote IDs to each object for tracking origin.
- Support multiple policy patterns in one contract.
- Add Unit tests and integration tests.
- Add quality checks and project reports to `pom.xml`: execute with `mvn verify site`.
- Improve contract negotiation and usage control.
  * Add contract agreement validation in `ContractAgreementHandler`.
  * Note pre-defined providers for contract offers in `ContractRequestHandler`.
  * Use contract agreements for policy enforcement.
  * Handle out contract agreements for multiple artifacts (targets) within one negotiation sequence.
  * Restrict agreement processing to confirmed agreements.
  * Add relation between artifacts and agreements.
- Add possibility to subscribe backend URLs for updates to a requested resource.

### Changed
- Support of IDS Infomodel v4.0.4 (direct import in `pom.xml`).
- Change IDS Framework version to v4.0.7.
- Http tracer is limited to 10000 characters per log line.
- Log file creation is disabled by default.
- Move Swagger UI to `/api/docs`.
- Change response type from string to object.
- Use correct response codes as defined by RFC 7231.
- Replace old data model: catalogs, resources, representations, artifacts, contract, rules, and
  agreements.
  * Separate `ResourceRepresentation` into `Representation` and `Artifact`.
  * Separate `ResourceContract` into `Contract` and `Rule`.
  * Handle data in own database entity.
  * Separate management of resources and its relations.
  * Define clear interfaces between data model and the IDS Infomodel objects.
  * Add IDS object builder classes.
    * Build ids:Resource only if at least 1 representation and 1 contract is present.
    * Build ids:Representation only if at least 1 artifact is present.
    * Build ids:ContractOffer only if at least 1 rule is present.
  * Move remote information from `BackendSource` to `Artifact`.
- Strict implementation of model view controller pattern for data management.
  * Controller methods for resources and representations.
  * Provide strict access control to backend. Information can only be read and changed by services.
  * Strict state validation for entities via factory classes.
- Change IDS messaging sequence: Start with `ContractRequestMessage` for automated
  `DescriptionRequestMessage` and `ArtifactRequestMessage`.
- Improve data transfer.
  * Process bytes instead of strings.
  * Remove limit for data in internal database.
  * Establish connection via `ArtifactRequestMessage` for always pulling recent data.

### Fixed
- Fix of buffer overflow in http tracer.
- Make message handler stateless.

### Security
- Prevent leaking of technology stack in case of errors/exceptions.
- Logger sanitizes inputs to prevent CRLF injections.
- Mass Bindings.
- Timezone independence.

## [4.3.1] - 2021-04-15

### Changed
- Set builder image to JDK 11.

## [4.3.0] - 2021-03-24

### Added
- Configure timeout values for http connections via `application.properties`.

## [4.2.0] - 2021-03-09

### Added
- New policy pattern: connector-restricted data usage.
- Validate `CONNECTOR_RESTRICTED_USAGE` on data request (as a provider).

## [4.1.0] - 2021-03-02

### Added
- Handle `ResourceUpdateMessage`: Update the local copy of resource upon receiving a `ResourceUpdateMessage`.
- Add attribute for endpoint documentation reference to `ResourceMetadata`.
- Store `ownerURI`, `contractID`, `artifactID`, and `originalUUID` in `RequestedResource`.
- Add support for query params, path variables, and additional headers when requesting artifacts.
- Add input validation for query params, path variables, and headers.
- Add usage control framework checking to the classes `PolicyEnforcement` and `PolicyHandler`.
- Add example files for deployment in Kubernetes.

### Changed
- Configure Spring to fail on unknown properties in request bodies.
- Move settings for policy negotiation and allowing unsupported patterns to `application.properties`.
- Refactor HttpUtils to use the IDS Framework's `HttpService`.
- Add data string as request body instead of request parameter.

### Fixed
- Exclusive use of the `ConfigurationContainer` for processing the connector's self-description and
  configurations to avoid state errors (relevant for the broker communication).

## [4.0.2] - 2021-02-04

### Added
- Add message handler for `ContractAgreementMessage`.

### Changed
- Answer with a `MessageProcessedNotificationMessage` to the consumer's `ContractAgreementMessage`.
- Save the `ContractAgreement` to the database and the Clearing House when the second
`AgreementMessage` has been processed.
- Refine exception handling in the message building and sending process.
- Update from IDS Framework v4.0.2 to v4.0.3.

### Fixed
- Send `ContractAgreementMessage` as request message.

## [4.0.1] - 2021-01-28

### Changed
- Update from IDS Framework v4.0.1 to v4.0.2.

## [4.0.0] - 2021-01-25

### Added
- Add public endpoint for self-description without resource catalog and public key.
- Add example endpoints.
- Add exceptions and detailed exception handling.
- Create `UUIDUtils` for uuid handling.
- Create `ControllerUtils` for http responses.
- Add endpoints for contract negotiation.
- Add http tracing and improved logging.
- Add custom profiles for Maven.
- Add negotiation service.
- Add Spring actuators.
- Add contract agreement repository.

### Changed
- Change object handling and model classes.
    - Move attribute `system` from `BackendSource` as `name` to `ResourceRepresentation`.
    - Move attribute `sourceType` from `ResourceRepresentation` as `type` to `BackendSource`.
    - Migrate `ResourceRepresentation` to map.
- Remove requested resource list from description response.
- Rename broker communication and self-description endpoints.
- Improve exception handling.
- Improve message handler and sending request messages in `de.fraunhofer.isst.dataspaceconnector.services.messages`.
- Change package structure.
- Add abstract class to resource service implementations.
- Edit policy handler.
- Improve `pom.xml`.
- Remove local caching of ids resources.
- Update to IDS Framework v4.0.1.
- Restructure `README.md` and wiki.
- Move code of conduct from `CONTRIBUTING.md` to `CODE_OF_CONDUCT.md`.
- Add response code annotations to endpoint methods.
- Change http response formatting.
- Replace Log4j1 with Log4j2.

### Fixed
- Update connector of configuration container before sending a broker message.
- Enforce access counter usage by moving it to an isolated method.

## [3.2.1] - 2020-11-05

### Changed
- Update to IDS framework v3.2.3.
- Move self-service and example endpoints to admin API.
- Improve Dockerfile.
- Add key- and truststore to example configuration.
- Add default policy (provide access) to resource on creation.

### Added
- Update and delete resources from broker.
- Add configuration controller for GET and PUT configuration model.
- Add possibility to set a resource id on create.
- Add possibility to set a representation id on create.
- Add a description of how the internal database can be replaced by another.
- Add .dockerignore file.

### Fixed
- Add representation.
- Fix token error in test classes.
- Fix file path in packaged jar.

## [3.2.0] - 2020-10-09

### Changed
- Change call to BrokerService constructor (parameters changed) in BrokerController.
- Change call to IDSHttpService constructor (parameters changed) in ConnectorRequestServiceImpl.
- Replace ConfigProducer with ConfigurationContainer in IdsUtils, MainController and DescriptionMessageHandler.

### Added
- Autowire ConfigurationContainer in constructor to instantiate Connector, KeyStoreManager or ConfigurationModel (previously directly autowired).
- Add ClientProvider field to all classes using an OkHttpClient (create instance in constructor from autowired ConfigurationContainer) & replace calls to IDSUtils.getClient() with clientProvider.getClient().
- Add file URI scheme to paths of KeyStore and TrustStore in config.json.
- Add test classes: SelfDescriptionTest, RequestDescriptionTest, RequestArtifactTest, DescriptionRequestMessageHandlingTest, ArtifactRequestMessageHandlingTest.

### Removed
- IDS Connector certificate file.

## [3.1.0] - 2020-09-29

### Changed
- Integrate IDS policy language.
- Modify policy patterns.
- Adapt policy reader to new policy language.
- Adapt usage control implementation to new patterns.

### Added
- Detailed Javadoc.
- Endpoint for example usage policies.
- Create NotificationMessageHandler for incoming notification messages. (TODO: not yet working, due to a pending IDS Framework update)

### Removed
- Old ODRL and policy resource examples.
- Custom ODRL model.
- Remove external backend application from test setups.

## [3.0.0] - 2020-09-02

### Changed
- Update to Information Model v4.0.0.
- Update to Framework v3.1.0.
- Replace config.yaml with config.json.
- Redesign resource model.
    - Add representations and multiple source types.
    - Distinguish between offered and requested resources in two separate database tables.
- Redesign connector open API.
    - Add contract endpoints.
    - Add representation endpoints.
    - Request data by resource id and endpoint id.
    - Removed parameters from connector requests.
- Add two separated resource services.
- Keep the self-description up to data at runtime with every resource change.
- Move the automated policy check to a separate policy enforcement class.
- Improve the requested metadata and data deserializing and saving.
- Improve http utils.
- Improve message handler.
    - Return description in IDS format with an odrl usage policy as property.
    - Request a specific artifact instead of a data resource.

### Removed
- Remove IDS configmodel.
- Remove unused communication service classes.
- Remove token service classes.

## [2.0.2] - 2020-08-26

### Changed
- Remove ISST private repository and settings.
- Add public ISST repository.

## [2.0.1] - 2020-08-03

### Added
- Add DAT request every hour.
- Add RequestMessageServiceUtils class for response handling.
- Add operator check on COUNT_ACCESS.
- Add h2 remote access.
- Add NO_POLICY pattern.
- Add prohibition of requested metadata update for data consumer.

### Changed
- Change odrl model (minimize).
- Changed requested metadata + data saving.

### Fixed
- Fix DescriptionMessageHandler policy check.
- Fix request DAT from DAPS.

## [2.0] - 2020-07-22

### Added
- Add data resource values: created, internal.
- Add odrl policy model.
- Add example odrl policies.
- Add requested resource (resourceMetadata + data) saving.
- Add policy pattern recognition: PROVIDE_ACCESS, TIME_INTERVAL, INHIBIT_ACCESS, LOG_ACCESS, COUNT_ACCESS, DELETE_AFTER (duration/date).
- Add usage control implementation as data provider: at artifact message handler (PROVIDE_ACCESS, TIME_INTERVAL, INHIBIT_ACCESS).
- Add usage control implementation as data consumer: at data access (TIME_INTERVAL, LOG_ACCESS, COUNT_ACCESS).
- Add automated usage control implementation as data consumer: COUNT_ACCESS, DELETE_AFTER (duration/date).

### Changed
- Change data resource model: separated uuid and uri from resourceMetadata.

## [1.0.1] - 2020-07-16

### Added
- Add connection to external data resource HTTP API.
- Add connection to external data resource HTTP API with basic auth.

### Changed
- Change naming to Dataspace Connector.
- Change DescriptionRequestHandler to return IDS metainformation.

## [1.0.0] - 2020-07-01

### Added
- Setup a basic Spring Boot application.
- Integrate the IDS Framework v2.0.12 and Infomodel v3.1.0.
- Provide REST endpoints for resource handling and IDS communication.
- Add a H2 database for saving resources (data & resourceMetadata).
- Provide a documentation for setting up the application with Maven and Docker.
- Add message handling for incoming ArtifactRequestMessages and DescriptionRequestMessages.
- Add IDS Broker communication (register, unregister, update, query).
- Setup a Swagger UI.
- Add basic authentication for the backend API.
- Add proxy and certification basic setup.
- Provide an example Postman collection.
