package io.dataspaceconnector.camel.processor.controller.helper;

import java.net.URI;

import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.service.ids.ConnectorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Finds the resource required for sending a ResourceUpdate- or ResourceUnavailableMessage and sets
 * it as the exchange's body.
 */
@Component("ResourceFinder")
@RequiredArgsConstructor
public
class ResourceFinder extends IdsHelperProcessor {

    /**
     * Service for ids resources.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Finds the resource with the ID from the exchange properties and sets it as the exchange's
     * body.
     * @param exchange the exchange.
     * @throws ResourceNotFoundException if no resource with the given ID exists.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws ResourceNotFoundException {
        final var resourceId = exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class);
        final var resource = connectorService.getOfferedResourceById(resourceId);

        if (resource.isEmpty()) {
            throw new ResourceNotFoundException("Could not find resource with ID " + resourceId);
        }

        exchange.getIn().setBody(resource.get());
    }
}
