package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ConfigurationModelImpl;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class IdsConnectorService {

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configContainer;

    /**
     * The ids resource service.
     */
    private final @NonNull IdsResourceService resourceService;

    /**
     * The token provider.
     */
    private final @NonNull DapsTokenProvider tokenProvider;

    /**
     * Build a base connector object with all offered resources.
     *
     * @return The ids base connector object.
     */
    public BaseConnector getConnectorWithOfferedResources() throws ConstraintViolationException {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();

        // Create a connector with a list of offered resources.
        final var connectorImpl = (BaseConnectorImpl) connector;
        connectorImpl.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
                ._offeredResource_((ArrayList<? extends Resource>)
                        resourceService.getAllOfferedResources())
                .build()));
        return connectorImpl;
    }

    /**
     * Build a base connector object with all offered and requested resources.
     *
     * @return The ids base connector object.
     */
    public BaseConnector getConnectorWithAllResources() throws ConstraintViolationException {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();

        // Create a connector with a list of offered resources.
        final var connectorImpl = (BaseConnectorImpl) connector;
        connectorImpl.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
                ._offeredResource_((ArrayList<? extends Resource>) resourceService.getAllOfferedResources())
                ._requestedResource_((ArrayList<? extends Resource>) resourceService.getAllRequestedResources())
                .build()));
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
     * Updates the connector object in the ids framework's config container.
     *
     * @throws ConfigurationUpdateException If the configuration could not be update.
     */
    public void updateConfigModel() throws ConfigurationUpdateException  {
        try {
            final var connector = getConnectorWithAllResources();
            final var configModel = (ConfigurationModelImpl) configContainer.getConfigModel();
            configModel.setConnectorDescription(connector);

            configContainer.updateConfiguration(configModel);
        } catch (ConstraintViolationException exception) {
            throw new ConfigurationUpdateException("Failed to retrieve connector.", exception);
        }
    }

    /**
     * Get a local copy of the current connector and extract its id.
     * @return The connector id.
     */
    public URI getConnectorId() {
        final var connector = configContainer.getConnector();
        return connector.getId();
    }

    /**
     * Get a local copy of the current connector and extract the outbound model version.
     * @return The outbound model version.
     */
    public String getOutboundModelVersion() {
        final var connector = configContainer.getConnector();
        return connector.getOutboundModelVersion();
    }

    /**
     * Get a local copy of the current connector and extract the inbound model versions.
     * @return A list of supported model versions.
     */
    public ArrayList<? extends String> getInboundModelVersion() {
        final var connector = configContainer.getConnector();
        return connector.getInboundModelVersion();
    }

    /**
     * Return current DAT.
     * @return The connector's DAT.
     */
    public DynamicAttributeToken getCurrentDat() {
        return tokenProvider.getDAT();
    }
}
