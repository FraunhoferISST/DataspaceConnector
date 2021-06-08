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
package io.dataspaceconnector.bootstrap;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.model.templates.ResourceTemplate;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.resources.CatalogService;
import io.dataspaceconnector.services.resources.TemplateBuilder;
import io.dataspaceconnector.utils.MessageUtils;
import io.dataspaceconnector.utils.TemplateUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.apache.commons.fileupload.MultipartStream;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static io.dataspaceconnector.bootstrap.BootstrapUtils.findFilesByExtension;
import static io.dataspaceconnector.bootstrap.BootstrapUtils.retrieveBootstrapConfig;

/**
 * This class allows to load JSON-LD files that contain IDS Infomodel representations of entities
 * which will be registered at the connector during start-up. Furthermore, an additional
 * configuration file can be loaded, that provides information on e.g. broker usage and the used
 * clearing house.
 */
@Component
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BootstrapConfiguration {

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
    private final @NotNull DeserializationService deserializationService;

    /**
     * The template builder.
     */
    private final @NotNull TemplateBuilder<OfferedResource, OfferedResourceDesc> templateBuilder;

    /**
     * The catalog service.
     */
    private final @NotNull CatalogService catalogService;

    /**
     * The platform transaction manager.
     */
    private final @NotNull PlatformTransactionManager transactionManager;

    /**
     * The service for communication with the ids broker.
     */
    private final @NotNull IDSBrokerService brokerService;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Bootstrap the connector. Will load JSON-LD files containing IDS catalog entities and register
     * them in the DSC. Additionally, property files will be loaded and provide information on the
     * clearing house and broker that should be used, and which resources need to be registered at
     * what broker.
     */
    @PostConstruct
    @Transactional
    public void bootstrap() {
        if (log.isInfoEnabled()) {
            log.info("Start bootstrapping of Connector.");
        }

        // Try to retrieve data and properties.
        List<File> jsonFiles = null;
        try {
            jsonFiles = findFilesByExtension(bootstrapPath, null, FILE_EXT);
            if (jsonFiles.isEmpty() && log.isWarnEnabled()) {
                log.warn("Catalog files for bootstrapping could not be loaded.");
            }
        } catch (FileNotFoundException | NullPointerException e) {
            if (log.isDebugEnabled()) {
                log.debug("No catalog files for bootstrapping found. [exception=({})]",
                        e.getMessage());
            }
        }

        var properties = new Properties();
        try {
            properties = retrieveBootstrapConfig(bootstrapPath, PROPERTIES_NAME, PROPERTIES_EXT);
            if (properties.isEmpty() && log.isWarnEnabled()) {
                log.warn("Config files for bootstrapping could not be loaded.");
            }
        } catch (FileNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("No config files for bootstrapping found. [exception=({})]",
                        e.getMessage());
            }
        }

        final Map<URI, Resource> idsResources = new ConcurrentHashMap<>();

        // register content of all found catalog files
        if (!processIdsFiles(jsonFiles, properties, idsResources)) {
            if (log.isWarnEnabled()) {
                log.warn("An error occurred while bootstrapping IDS catalogs.");
            }
            SpringApplication.exit(context, () -> -1);
        }

        try {
            connectorService.updateConfigModel();
        } catch (ConfigurationUpdateException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to update config model.", e);
            }
        }

        // register resources at broker
        if (!registerAtBroker(properties, idsResources)) {
            if (log.isErrorEnabled()) {
                log.error("An error occurred while registering resources at the broker.");
            }
            SpringApplication.exit(context, () -> -1);
        }

        if (log.isInfoEnabled()) {
            log.info("Finished bootstrapping of connector.");
        }
    }

    private boolean registerAtBroker(final Properties properties,
                                     final Map<URI, Resource> idsResources) {
        final var knownBrokers = new HashSet<String>();
        // iterate over all registered resources
        for (final var entry : idsResources.entrySet()) {
            final var propertyKey = "broker.register." + entry.getKey().toString();
            if (properties.containsKey(propertyKey)) {
                final var brokerURL = (String) properties.get(propertyKey);

                try {
                    Response response;
                    if (!knownBrokers.contains(brokerURL)) {
                        knownBrokers.add(brokerURL);
                        response = brokerService.updateSelfDescriptionAtBroker(brokerURL);
                        if (validateBrokerResponse(response, brokerURL)) {
                            if (log.isInfoEnabled()) {
                                log.info("Registered connector at broker '{}'.", brokerURL);
                            }
                        } else {
                            return false;
                        }
                    }

                    response = brokerService.updateResourceAtBroker(brokerURL, entry.getValue());
                    if (!response.isSuccessful()) {
                        if (log.isErrorEnabled()) {
                            log.error("Failed to update resource description for resource '{}'"
                                            + " at broker '{}'.",
                                    entry.getValue().getId().toString(), brokerURL);
                        }

                        return false;
                    }
                    if (validateBrokerResponse(response, brokerURL)) {
                        if (log.isInfoEnabled()) {
                            log.info("Registered resource with IDS ID '{}' at broker '{}'.",
                                    entry.getKey().toString(), brokerURL);
                        }
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Could not register resource with IDS id '{}' at the "
                                + "broker '{}'.", entry.getKey().toString(), brokerURL, e);
                    }

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Check if a broker request was successfully processed by a broker.
     *
     * @param response  the broker response
     * @param brokerURL the URL of the called broker
     * @return true if the broker successfully processed the message, false otherwise
     * @throws IOException if the response's body cannot be extracted as string.
     */
    private boolean validateBrokerResponse(final Response response, final String brokerURL)
            throws IOException {
        if (!response.isSuccessful()) {
            if (log.isErrorEnabled()) {
                log.error("Failed to sent message to a broker '{}'.", brokerURL);
            }

            return false;
        }

        final var responseBody = response.body();
        if (responseBody == null) {
            if (log.isErrorEnabled()) {
                log.error("Could not parse response after sending a request "
                        + "to a broker.");
            }

            return false;
        }

        final var body = responseBody.string();
        final var responseMessage = getMessage(body);
        if (responseMessage.isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("Could not parse response after sending a request "
                        + "to a broker.");
            }

            return false;
        }

        if (!(responseMessage.get() instanceof MessageProcessedNotificationMessage)) {
            if (responseMessage.get() instanceof RejectionMessage) {
                final var payload = getMultipartPart(body, "payload");
                if (log.isErrorEnabled() && payload.isPresent()) {
                    log.error("The broker rejected the message. Reason: {} - {}",
                            MessageUtils.extractRejectionReason(
                                    (RejectionMessage) responseMessage.get()).toString(),
                                    payload.get());
                } else if (log.isErrorEnabled()) {
                    log.error("The broker rejected the message. Reason: {}",
                            MessageUtils.extractRejectionReason(
                                        (RejectionMessage) responseMessage.get()).toString());
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error("An error occurred while registering the "
                            + "connector at the broker.");
                }
            }
            return false;
        }

        return true;
    }

    /**
     * Extract the IDS message from a multipart message string.
     *
     * @param body a multipart message
     * @return The IDS message contained in the multipart message, null if any error occurs.
     */
    private Optional<Message> getMessage(final String body) {
        final var part = getMultipartPart(body, "header");
        if (part.isPresent()) {
            return Optional.of(deserializationService.getMessage(part.get()));
        } else {
            if (log.isErrorEnabled()) {
                log.error("Could not find IDS message in multipart message.");
            }

            return Optional.empty();
        }
    }

    /**
     * Extract a part with given name from a multipart message.
     *
     * @param message  the multipart message
     * @param partName the part name
     * @return part with given name, null if the part does not exist in given message
     */
    private Optional<String> getMultipartPart(final String message, final String partName) {
        try {
            // TODO: Can we get the original charset of the message?
            final var multipart = new MultipartStream(
                    new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)),
                    getBoundaries(message)[0].substring(2).getBytes(StandardCharsets.UTF_8),
                    4096,
                    null
            );

            final var pattern = Pattern.compile("name=\"([a-zA-Z]+)\"");
            final var outputStream = new ByteArrayOutputStream();
            boolean next = multipart.skipPreamble();
            while (next) {
                final var matcher = pattern.matcher(multipart.readHeaders());
                if (!matcher.find()) {
                    if (log.isErrorEnabled()) {
                        log.error("Could not find name of multipart part.");
                    }
                    return Optional.empty();
                }

                if (matcher.group().equals("name=\"" + partName + "\"")) {
                    multipart.readBodyData(outputStream);
                    return Optional.of(outputStream.toString(StandardCharsets.UTF_8));
                } else {
                    multipart.discardBodyData();
                }

                next = multipart.readBoundary();
            }

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to parse multipart message.", e);
            }
            return Optional.empty();
        }

        if (log.isErrorEnabled()) {
            log.error("Could not find part '{}' in multipart message.", partName);
        }
        return Optional.empty();
    }

    private String[] getBoundaries(final String msg) {
        return msg.split(msg.contains("\r\n") ? "\r\n" : "\n");
    }

    /**
     * Load a list of JSON-LD files which contain ids catalog entities and
     * register them at the connector.
     *
     * @param jsonFiles    List of JSON-LD files to load
     * @param properties   additional properties which are required but not
     *                     present in ids representations
     * @param idsResources (IDS ID, IDS-Resource) map that cointains bootstrapped elements
     * @return true if all catalogs were loaded successfully or were already
     * registered, false otherwise
     */
    private boolean processIdsFiles(final List<File> jsonFiles, final Properties properties,
                                    final Map<URI, Resource> idsResources) {
        final Set<ResourceCatalog> catalogs = new HashSet<>();

        // deserialize all files
        for (final var jsonFile : jsonFiles) {
            try {
                catalogs.add(deserializationService.getResourceCatalog(
                        Files.readString(jsonFile.toPath()))
                );
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Could not deserialize ids catalog file '{}'.",
                            jsonFile.getPath(), e);
                }
                return false;
            }
        }

        final var template = new TransactionTemplate(transactionManager);
        // iterate over all deserialized catalogs
        for (final ResourceCatalog catalog : catalogs) {
            // check for duplicates
            // get all known catalogs for every bootstrap
            // processor to detect duplicated bootstrap files
            final Boolean duplicate = template.execute(transactionStatus -> {
                Page<Catalog> knownCatalogs = catalogService.getAll(Pageable.unpaged());
                boolean catalogDuplicate = false;
                for (Catalog knownCatalog : knownCatalogs) {
                    Hibernate.initialize(knownCatalog.getAdditional());
                    if (catalog.getId()
                            .equals(knownCatalog.getBootstrapId())) {
                        catalogDuplicate = true;
                        break;
                    }
                }
                return catalogDuplicate;
            });

            if (duplicate != null && duplicate) {
                if (log.isInfoEnabled()) {
                    log.info("Catalog with IDS id '{}' is already registered and will be"
                            + " skipped.", catalog.getId());
                }
                continue;
            }

            if (!registerCatalog(catalog, properties, idsResources)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Transform an ids resource catalog to dsc format and register the catalog
     * in the connector.
     *
     * @param catalog      the ids resource catalog entity
     * @param properties   additional properties which are missing in ids entity
     * @param idsResources (IDS ID, IDS-Resource) map that cointains bootstrapped elements
     * @return true if the catalog could be registered, false otherwise
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

        // requested resources are skipped
        final var requestedResources = new ArrayList<ResourceTemplate<RequestedResourceDesc>>();
        catalogTemplate.setRequestedResources(requestedResources);

        // perform registration
        templateBuilder.build(catalogTemplate);

        if (log.isInfoEnabled()) {
            log.info("Bootstrapped catalog with IDS id '{}'.", catalog.getId());
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
    private void fillResourceTemplate(
            final ResourceTemplate<OfferedResourceDesc> resourceTemplate,
            final Properties properties,
            final Resource resource) {
        // add ids id to additional fields
        resourceTemplate.getDesc().setBootstrapId(resource.getId().toString());

        // collect all artifact IDs from artifacts inside representations
        resourceTemplate.setRepresentations(
                TemplateUtils.getRepresentationTemplates(
                        resource,
                        collectArtifactUris(resource),
                        shouldAutoDownload(properties, resource.getId()),
                        null
                )
        );

        resourceTemplate.setContracts(TemplateUtils.getContractTemplates(resource));

        // add additional information from properties to artifact descriptions
        for (final var representationTemplate : resourceTemplate.getRepresentations()) {
            for (final var artifactTemplate : representationTemplate.getArtifacts()) {
                final var bootstrapId = artifactTemplate.getDesc().getBootstrapId();
                final var accessUrl = properties.getProperty("artifact.accessUrl." + bootstrapId);
                final var username = properties.getProperty("artifact.username." + bootstrapId);
                final var password = properties.getProperty("artifact.password." + bootstrapId);
                final var value = properties.getProperty("artifact.value." + bootstrapId);

                if (accessUrl != null) {
                    try {
                        artifactTemplate.getDesc().setAccessUrl(new URL(accessUrl));
                    } catch (MalformedURLException e) {
                        if (log.isErrorEnabled()) {
                            log.error("Could not parse accessUrl '{}' for artifact with "
                                    + "IDS id '{}'.", accessUrl, bootstrapId, e);
                        }
                    }
                }

                if (username != null) {
                    artifactTemplate.getDesc().setUsername(username);
                }

                if (password != null) {
                    artifactTemplate.getDesc().setPassword(password);
                }

                if (value != null) {
                    artifactTemplate.getDesc().setValue(value);
                }
            }
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
