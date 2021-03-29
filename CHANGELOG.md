# Changelog
All notable changes to this project will be documented in this file.

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
