package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.ids;

import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
     * Build a base connector object with all offered resources.
     *
     * @return The ids base connector object.
     */
    public BaseConnector getConnectorWithOfferedResources() {
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
    public BaseConnector getConnectorWithAllResources() {
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
    public BaseConnector getConnectorWithoutResources() {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();

        // Create a connector without any resources.
        final var connectorImpl = (BaseConnectorImpl) connector;
        connectorImpl.setResourceCatalog(null);
        return connectorImpl;
    }
}
