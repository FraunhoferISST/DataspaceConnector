/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.extension.bootstrap;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.ids.messaging.common.DeserializeException;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.http.SendMessageException;
import de.fraunhofer.ids.messaging.protocol.http.ShaclValidatorException;
import de.fraunhofer.ids.messaging.protocol.multipart.UnknownResponseException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import de.fraunhofer.ids.messaging.requests.exceptions.NoTemplateProvidedException;
import de.fraunhofer.ids.messaging.requests.exceptions.RejectionException;
import de.fraunhofer.ids.messaging.requests.exceptions.UnexpectedPayloadException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.model.TemplateUtils;
import io.dataspaceconnector.extension.bootstrap.util.BootstrapUtils;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.broker.BrokerDesc;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.service.message.GlobalMessageService;
import io.dataspaceconnector.service.resource.templatebuilder.CatalogTemplateBuilder;
import io.dataspaceconnector.service.resource.type.BrokerService;
import io.dataspaceconnector.service.resource.type.CatalogService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.dataspaceconnector.extension.bootstrap.util.BootstrapUtils.findFilesByExtension;
import static io.dataspaceconnector.extension.bootstrap.util.BootstrapUtils.retrieveBootstrapConfig;

/**
 * This class allows to load JSON-LD files that contain IDS representations of entities
 * which will be registered at the connector during start-up. Furthermore, an additional
 * configuration file can be loaded, that provides information on e.g. broker usage and the used
 * clearing house.
 */
@Component
@Log4j2
@RequiredArgsConstructor
@Transactional
public class Bootstrapper {

    /**
     * File extension used for JSON-LD files.
     */
    private static final String FILE_EXT = "jsonld";

    /**
     * File name for bootstrap property files.
     */
    private static final String PROPERTIES_NAME = "bootstrap";

    /**
     * File extension used for bootstrap property files.
     */
    private static final String PROPERTIES_EXT = "properties";

    /**
     * Root where search for bootstrapping file starts.
     */
    @Value("${bootstrap.path}")
    private String bootstrapPath;

    /**
     * Spring application context. Needed for shutdowns in case of errors.
     */
    private final @NotNull ApplicationContext context;

    /**
     * Service for deserializing ids entities.
     */
    private final @NotNull DeserializationService deserializationSvc;

    /**
     * The template builder.
     */
    private final @NotNull CatalogTemplateBuilder templateBuilder;

    /**
     * The catalog service.
     */
    private final @NotNull CatalogService catalogSvc;

    /**
     * The platform transaction manager.
     */
    private final @NotNull PlatformTransactionManager transactionManager;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorSvc;

    /**
     * Service for interacting with a broker.
     */
    private final @NonNull GlobalMessageService brokerSvc;

    /**
     * Service for the broker.
     */
    private final @NotNull BrokerService brokerService;

    /**
     * Bootstrap the connector. Will load JSON-LD files containing IDS catalog entities and register
     * them in the DSC. Additionally, property files will be loaded and provide information on the
     * clearing house and broker that should be used, and which resources need to be registered at
     * what broker.
     */
    public void bootstrap() {
        if (log.isInfoEnabled()) {
            log.info("Start bootstrapping of Connector.");
        }

        // register content of all found catalog files
        final var properties = loadProperties();
        final var idsResources = new ConcurrentHashMap<URI, Resource>();
        if (!processIdsFiles(loadBootstrapData(), properties, idsResources)) {
            if (log.isWarnEnabled()) {
                log.warn("An error occurred while bootstrapping IDS catalogs.");
            }
            SpringApplication.exit(context, () -> -1);
        }

        try {
            connectorSvc.updateConfigModel();
        } catch (ConfigUpdateException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to update config model. [exception=({})]", e.getMessage(), e);
            }
        }

        // register resources at broker
        if (!registerAtBroker(properties, idsResources)) {
            if (log.isInfoEnabled()) {
                log.info("An error occurred while registering resources at the broker.");
            }
        }

        if (log.isInfoEnabled()) {
            log.info("Finished bootstrapping of connector.");
        }
    }

    /**
     * Register resources at the broker.
     *
     * @param properties Bootstrap properties.
     * @param resources  The ids resources to register.
     * @return true if all resources could be registered.
     */
    private boolean registerAtBroker(final Properties properties,
                                    final Map<URI, Resource> resources) {
        final var knownBrokers = new HashSet<String>();
        // Iterate over all registered resources.
        for (final var entry : resources.entrySet()) {
            final var propertyKey = "broker.register." + entry.getKey().toString();
            if (properties.containsKey(propertyKey)) {
                final var brokerUrl = BootstrapUtils.toUrl(properties.getProperty(propertyKey));
                if (brokerUrl.isEmpty()) {
                    if (log.isWarnEnabled()) {
                        log.warn("Skipping broker due to invalid url. [broker=({})]",
                                properties.getProperty(propertyKey));
                    }
                    return false;
                }

                final var broker = brokerUrl.get();

                try {
                    if (!knownBrokers.contains(broker.toString())) {
                        knownBrokers.add(broker.toString());

                        createBroker(broker);

                        var connectorResponse = brokerSvc
                                .sendConnectorUpdateMessage(broker.toURI());
                        if (!brokerSvc.checkResponse(connectorResponse)) {
                            return false;
                        }
                    }

                    var resourceResponse = brokerSvc
                            .sendResourceUpdateMessage(broker.toURI(), entry.getValue());
                    if (!brokerSvc.checkResponse(resourceResponse)) {
                        return false;
                    }
                } catch (MultipartParseException | ClaimsException | DapsTokenManagerException
                        | IOException | URISyntaxException | NoTemplateProvidedException
                        | ShaclValidatorException | SendMessageException
                        | UnexpectedPayloadException | SerializeException
                        | DeserializeException | RejectionException
                        | UnknownResponseException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Could not register resource at broker [resourceId=({}), "
                                + "broker=({})].", entry.getKey().toString(), broker, e);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private void createBroker(final URL broker) throws URISyntaxException {
        if (brokerService.findByLocation(broker.toURI()).isEmpty()) {
            final var brokerDesc = new BrokerDesc();
            brokerDesc.setLocation(broker.toURI());
            brokerDesc.setTitle(broker.toString());
            brokerService.create(brokerDesc);
        }
    }

    private List<File> loadBootstrapData() {
        try {
            final var files = findFilesByExtension(bootstrapPath, FILE_EXT);
            if (files.isEmpty() && log.isWarnEnabled()) {
                log.warn("Catalog files for bootstrapping could not be loaded.");
            }

            return files;
        } catch (FileNotFoundException | NullPointerException e) {
            if (log.isDebugEnabled()) {
                log.debug("No catalog files for bootstrapping found. [exception=({})]",
                        e.getMessage(), e);
            }
        }
        return new ArrayList<>();
    }

    private Properties loadProperties() {
        var properties = new Properties();
        try {
            properties = retrieveBootstrapConfig(bootstrapPath, PROPERTIES_NAME, PROPERTIES_EXT);
            if (properties.isEmpty() && log.isWarnEnabled()) {
                log.warn("Config files for bootstrapping could not be loaded.");
            }
        } catch (FileNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("No config files for bootstrapping found. [exception=({})]",
                        e.getMessage(), e);
            }
        }

        return properties;
    }

    /**
     * Load a list of JSON-LD files which contain ids catalog entities and
     * register them at the connector.
     *
     * @param jsonFiles    List of JSON-LD files to load
     * @param properties   additional properties which are required but not
     *                     present in ids representations
     * @param idsResources (IDS ID, IDS-Resource) map that contains bootstrapped elements
     * @return true if all catalogs were loaded successfully or were already
     * registered, false otherwise
     */
    private boolean processIdsFiles(final List<File> jsonFiles, final Properties properties,
                                    final Map<URI, Resource> idsResources) {
        final var catalogs = deserializeAllCatalogs(jsonFiles);
        if (catalogs.isEmpty()) {
            return false;
        }

        final var template = new TransactionTemplate(transactionManager);
        // Iterate over all deserialized catalogs.
        for (final var catalog : catalogs.get()) {
            // Check for duplicates. Get all known catalogs for every bootstrap processor to detect
            // duplicated bootstrap files.
            final var duplicate = template.execute(x -> isDuplicate(catalog));

            if (duplicate != null && duplicate) {
                if (log.isInfoEnabled()) {
                    log.info("Catalog is already registered and will be skipped. "
                            + "[catalogId=({})]", catalog.getId());
                }
                continue;
            }

            if (!registerCatalog(catalog, properties, idsResources)) {
                return false;
            }
        }

        return true;
    }

    private boolean isDuplicate(final ResourceCatalog catalog) {
        for (final var knownCatalog : catalogSvc.getAll(Pageable.unpaged())) {
            Hibernate.initialize(knownCatalog.getAdditional());
            if (catalog.getId().equals(knownCatalog.getBootstrapId())) {
                return true;
            }
        }

        return false;
    }

    private Optional<Set<ResourceCatalog>> deserializeAllCatalogs(final List<File> jsonFiles) {
        final var catalogs = new HashSet<ResourceCatalog>();

        // Deserialize all files.
        for (final var jsonFile : jsonFiles) {
            try {
                catalogs.add(deserializationSvc.getResourceCatalog(
                        Files.readString(jsonFile.toPath())));
            } catch (IOException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not deserialize ids catalog file. [path=({})]",
                            jsonFile.getPath(), e);
                }
                return Optional.empty();
            }
        }
        return Optional.of(catalogs);
    }

    /**
     * Transform an ids resource catalog to dsc format and register the catalog in the connector.
     *
     * @param catalog      The ids resource catalog entity.
     * @param properties   Additional properties that are missing in ids entity.
     * @param idsResources (IDS ID, IDS-Resource) Map that contains bootstrapped elements.
     * @return true if the catalog could be registered, false otherwise.
     */
    @Transactional
    protected boolean registerCatalog(final ResourceCatalog catalog,
                                      final Properties properties,
                                      final Map<URI, Resource> idsResources) {
        // create templates
        final var catalogTemplate = TemplateUtils.getCatalogTemplate(catalog);

        final var offeredResources = new ArrayList<ResourceTemplate<OfferedResourceDesc>>();
        for (final var resource : catalog.getOfferedResource()) {
            final var resourceTemplate =
                    TemplateUtils.getOfferedResourceTemplate(resource);
            fillResourceTemplate(resourceTemplate, properties, resource);
            offeredResources.add(resourceTemplate);
            idsResources.put(resource.getId(), resource);
        }
        catalogTemplate.setOfferedResources(offeredResources);

        // Requested resources are skipped.
        final var requestedResources = new ArrayList<ResourceTemplate<RequestedResourceDesc>>();
        catalogTemplate.setRequestedResources(requestedResources);

        // Perform registration.
        templateBuilder.build(catalogTemplate);

        if (log.isInfoEnabled()) {
            log.info("Bootstrapped catalog. [catalogId=({})]", catalog.getId());
        }

        return true;
    }

    /**
     * Extract representations, artifacts, and contract offers for
     * a resource template from an ids resource.
     *
     * @param resourceTemplate the resource template
     * @param properties       additional properties, required for
     *                         transformation
     * @param resource         the ids resource
     */
    private void fillResourceTemplate(final ResourceTemplate<OfferedResourceDesc> resourceTemplate,
                                      final Properties properties,
                                      final Resource resource) {
        // Add ids id to additional fields.
        resourceTemplate.getDesc().setBootstrapId(resource.getId());

        // Collect all artifact IDs from artifacts inside representations.
        resourceTemplate.setRepresentations(TemplateUtils.getRepresentationTemplates(
                resource,
                collectArtifactUris(resource),
                shouldAutoDownload(properties, resource.getId()),
                null
        ));

        resourceTemplate.setContracts(TemplateUtils.getContractTemplates(resource));

        // Add additional information from properties to artifact descriptions.
        for (final var representationTemplate : resourceTemplate.getRepresentations()) {
            for (final var artifactTemplate : representationTemplate.getArtifacts()) {
                updateArtifactDesc(artifactTemplate.getDesc(), properties);
            }
        }
    }

    private void updateArtifactDesc(final ArtifactDesc desc, final Properties properties) {
        final var bootstrapId = desc.getBootstrapId();
        final var accessUrl = properties.getProperty("artifact.accessUrl." + bootstrapId);
        final var username = properties.getProperty("artifact.username." + bootstrapId);
        final var password = properties.getProperty("artifact.password." + bootstrapId);
        final var value = properties.getProperty("artifact.value." + bootstrapId);

        if (accessUrl != null) {
            try {
                desc.setAccessUrl(new URL(accessUrl));
            } catch (MalformedURLException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not parse accessUrl for artifact. [accessUrl=({}), "
                                    + "artifactId=({}), exception=({})]", accessUrl, bootstrapId,
                            e.getMessage(), e);
                }
            }
        }

        if (username != null || password != null) {
            desc.setBasicAuth(new AuthenticationDesc(username, password));
        }

        if (value != null) {
            desc.setValue(value);
        }
    }

    private List<URI> collectArtifactUris(final Resource resource) {
        final var artifacts = new ArrayList<URI>();
        for (final var representation : resource.getRepresentation()) {
            for (final var artifact : representation.getInstance()) {
                artifacts.add(artifact.getId());
            }
        }

        return artifacts;
    }

    private boolean shouldAutoDownload(final Properties properties, final URI resourceId) {
        return properties.containsKey("resource.download.auto")
                && properties.getProperty("resource.download.auto").contains(resourceId.toString());
    }
}
