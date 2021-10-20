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
package io.dataspaceconnector.common.ids;

import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ConfigurationModelImpl;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import de.fraunhofer.ids.messaging.core.config.ssl.keystore.KeyStoreManager;
import de.fraunhofer.ids.messaging.core.daps.ConnectorMissingCertExtensionException;
import de.fraunhofer.ids.messaging.core.daps.DapsConnectionException;
import de.fraunhofer.ids.messaging.core.daps.DapsEmptyResponseException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenProvider;
import io.dataspaceconnector.common.ids.mapping.FromIdsObjectMapper;
import io.dataspaceconnector.model.configuration.ConnectorStatus;
import io.dataspaceconnector.model.configuration.DeployMode;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.resource.ids.builder.IdsCatalogBuilder;
import io.dataspaceconnector.service.resource.ids.builder.IdsResourceBuilder;
import io.dataspaceconnector.service.resource.type.CatalogService;
import io.dataspaceconnector.service.resource.type.OfferedResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This service offers different methods related to the connector configuration, like e.g. getting
 * configuration properties or updating the configuration model.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ConnectorService {

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigContainer configContainer;

    /**
     * The token provider.
     */
    private final @NonNull DapsTokenProvider tokenProvider;

    /**
     * Service for persisted catalogs.
     */
    private final @NonNull CatalogService catalogService;

    /**
     * Service for ids views.
     */
    private final @NonNull IdsCatalogBuilder catalogBuilder;

    /**
     * Service for ids resources.
     */
    private final @NonNull IdsResourceBuilder<OfferedResource> resourceBuilder;

    /**
     * Service for offered resources.
     */
    private final @NonNull OfferedResourceService offeredResourceService;

    /**
     * Get keystore manager from ids messaging services.
     * @return The keystore manager.
     */
    public KeyStoreManager getKeyStoreManager() {
        return configContainer.getKeyStoreManager();
    }

    /**
     * Get a local copy of the current connector and extract its id.
     *
     * @return The connector id.
     */
    public URI getConnectorId() {
        final var connector = configContainer.getConnector();
        return connector.getId();
    }

    /**
     * Get the current connector status.
     *
     * @return The connector status.
     */
    public ConnectorStatus getConnectorStatus() {
        final var connector = configContainer.getConfigurationModel();
        final var status = connector.getConnectorStatus();

        return FromIdsObjectMapper.fromIdsConnectorStatus(status);
    }

    /**
     * Get the current deploy method.
     *
     * @return The connector's deploy method.
     */
    public DeployMode getDeployMethod() {
        final var config = configContainer.getConfigurationModel();
        final var deployMethod = config.getConnectorDeployMode();

        return FromIdsObjectMapper.fromIdsDeployMode(deployMethod);
    }

    /**
     * Get a local copy of the current connector and extract the outbound model version.
     *
     * @return The outbound model version.
     */
    public String getOutboundModelVersion() {
        final var connector = configContainer.getConnector();
        return connector.getOutboundModelVersion();
    }

    /**
     * Get a local copy of the current connector and extract the inbound model versions.
     *
     * @return A list of supported model versions.
     */
    public List<? extends String> getInboundModelVersion() {
        final var connector = configContainer.getConnector();
        return connector.getInboundModelVersion();
    }

    /**
     * Return current DAT.
     *
     * @return The connector's DAT.
     */
    public DynamicAttributeToken getCurrentDat() {
        try {
            return tokenProvider.getDAT();
        } catch (ConnectorMissingCertExtensionException e) {
            if (log.isWarnEnabled()) {
                log.warn("Connector certificate is missing aki/ski extensions."
                        + " [exception=({})]", e.getMessage());
            }
        } catch (DapsConnectionException e) {
            if (log.isWarnEnabled()) {
                log.warn("Connection to DAPS could not be established. "
                        + "[exception=({})]", e.getMessage());
            }
        } catch (DapsEmptyResponseException e) {
            if (log.isWarnEnabled()) {
                log.warn("Received empty response from DAPS. [exception=({})]", e.getMessage());
            }
        }

        return null;
    }

    /**
     * Build a base connector object with all offered resources.
     *
     * @return The ids base connector object.
     */
    public BaseConnector getConnectorWithOfferedResources() throws ConstraintViolationException {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();
        final var catalogs = getAllCatalogsWithOfferedResources();

        // Create a connector with a list of offered resources.
        final var connectorImpl = (BaseConnectorImpl) connector;
        connectorImpl.setResourceCatalog(catalogs);
        return connectorImpl;
    }

    /**
     * Build a base connector object without resources.
     *
     * @return The ids base connector object.
     */
    public BaseConnector getConnectorWithoutResources() throws ConstraintViolationException {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();

        // Create a connector without any resources.
        final var connectorImpl = (BaseConnectorImpl) connector;
        connectorImpl.setResourceCatalog(null);
        return connectorImpl;
    }

    /**
     * Updates the connector object in the ids messaging service's config container.
     *
     * @throws ConfigUpdateException If the configuration could not be update.
     */
    @Transactional
    public void updateConfigModel() throws ConfigUpdateException {
        try {
            final var connector = getConnectorWithOfferedResources();
            final var configModel = (ConfigurationModelImpl) configContainer
                    .getConfigurationModel();
            configModel.setConnectorDescription(connector);

            // Handled at a higher level.
            configContainer.updateConfiguration(configModel);
        } catch (ConstraintViolationException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to retrieve connector. [exception=({})]", e.getMessage(), e);
            }
            throw new ConfigUpdateException("Failed to retrieve connector.", e);
        }
    }

    /**
     * Get all catalogs with offered resources.
     *
     * @return List of resource catalogs.
     */
    private List<ResourceCatalog> getAllCatalogsWithOfferedResources() {
        return catalogService.getAll(Pageable.unpaged())
                .stream()
                .map(x -> catalogBuilder.create(x, 0))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Get offered resource by its id.
     *
     * @param resourceId The resource id.
     * @return The ids resource.
     */
    public Optional<Resource> getOfferedResourceById(final URI resourceId) {
        final var resource = offeredResourceService.getAll(Pageable.unpaged())
                .stream()
                .filter(x -> resourceId.toString().contains(x.getId().toString()))
                .findAny();

        return resource.map(resourceBuilder::create);
    }
}
