package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.jooq.Meta;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Base class for creating and updating endpoints.
 *
 * @param <T> The endpoint type.
 * @param <D> The description type.
 */
@Component
public abstract class EndpointFactory<T extends Endpoint, D extends EndpointDesc<T>>
        implements AbstractFactory<T, D> {

    /**
     * The default uri.
     */
    private static final URI DEFAULT_URI = URI.create("https://documentation");
    /**
     * The default string.
     */
    private static final String DEFAULT_STRING = "default";


    /**
     * Create a new endpoint.
     *
     * @param desc The description of the new endpoint.
     * @return The new endpoint.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public T create(final D desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var endpoint = createInternal(desc);
        update(endpoint, desc);

        return endpoint;
    }

    /**
     * Create a new endpoint. Implement type specific stuff here.
     *
     * @param desc The description passed to the factory.
     * @return The new resource.
     */
    protected abstract T createInternal(D desc);

    /**
     * @param endpoint The entity to be updated.
     * @param desc     The description of the new entity.
     * @return True, if entity is updated.
     */
    @Override
    public boolean update(final T endpoint, final D desc) {
        Utils.requireNonNull(endpoint, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedDocumentation = updateEndpointDocumentation(endpoint,
                desc.getEndpointDocumentation());
        final var hasUpdatedInformation = updateEndpointInformation(endpoint,
                endpoint.getEndpointInformation());
        final var hasUpdatedInboundPath = updateInboundPath(endpoint, endpoint.getInboundPath());
        final var hasUpdatedOutboundPath = updateOutboundPath(endpoint, endpoint.getOutboundPath());
        final var hasUpdatedEndpointType = updateEndpointType(endpoint, endpoint.getEndpointType());

        return hasUpdatedDocumentation || hasUpdatedInformation || hasUpdatedInboundPath || hasUpdatedOutboundPath
                || hasUpdatedEndpointType;
    }

    /**
     * @param endpoint     The endpoint.
     * @param endpointType The endpoint type.
     * @return True, if endpoint is updated.
     */
    private boolean updateEndpointType(Endpoint endpoint, EndpointType endpointType) {
        final boolean updated;
        if (endpoint.getEndpointType().equals(endpointType)) {
            updated = false;
        } else {
            endpoint.setEndpointType(endpointType);
            updated = true;
        }
        return updated;
    }

    /**
     * @param endpoint     The endpoint entity.
     * @param outboundPath The outbound path of the entity.
     * @return True, if outbound path is updated.
     */
    private boolean updateOutboundPath(final Endpoint endpoint, final String outboundPath) {
        final var newOutboundPath = MetadataUtils.updateString(endpoint.getOutboundPath(),
                outboundPath, DEFAULT_STRING);
        newOutboundPath.ifPresent(endpoint::setOutboundPath);

        return newOutboundPath.isPresent();
    }

    /**
     * @param endpoint    The endpoint entity.
     * @param inboundPath The inbound path of the entity.
     * @return True, if inbound path is updated.
     */
    private boolean updateInboundPath(final Endpoint endpoint, final String inboundPath) {
        final var newInboundPath = MetadataUtils.updateString(endpoint.getInboundPath(),
                inboundPath, DEFAULT_STRING);
        newInboundPath.ifPresent(endpoint::setInboundPath);

        return newInboundPath.isPresent();
    }

    /**
     * @param endpoint            The endpoint entity.
     * @param endpointInformation The endpoint information.
     * @return True, if endpoint information is updated.
     */
    private boolean updateEndpointInformation(final Endpoint endpoint, final String endpointInformation) {
        final var newEndpointInfo = MetadataUtils.updateString(endpoint.getEndpointInformation(),
                endpointInformation, DEFAULT_STRING);
        newEndpointInfo.ifPresent(endpoint::setEndpointInformation);

        return newEndpointInfo.isPresent();
    }


    /**
     * @param endpoint              The endpoint entity.
     * @param endpointDocumentation The endpoint documentation.
     * @return True, if endpoint documentation is updated.
     */
    private boolean updateEndpointDocumentation(final Endpoint endpoint, final URI endpointDocumentation) {
        final var newDocumenation = MetadataUtils.updateUri(endpoint.getEndpointDocumentation(),
                endpointDocumentation, DEFAULT_URI);
        newDocumenation.ifPresent(endpoint::setEndpointDocumentation);

        return newDocumenation.isPresent();
    }
}
