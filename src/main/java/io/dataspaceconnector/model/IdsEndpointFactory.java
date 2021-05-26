package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Creates and updates ids endpoints.
 */
@Component
public class IdsEndpointFactory extends EndpointFactory<IdsEndpoint, IdsEndpointDesc> {

    /**
     * Default absolute path.
     */
    private final static URI DEFAULT_URI = URI.create("https://path");

    /**
     * @param idsEndpoint The ids endpoint.
     * @param desc        The description of the new entity.
     * @return True, if ids endpoint is updated.
     */
    @Override
    public boolean update(final IdsEndpoint idsEndpoint, final IdsEndpointDesc desc) {
        Utils.requireNonNull(idsEndpoint, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        return updateAccessURL(idsEndpoint, desc.getAccessURL());
    }

    /**
     * @param idsEndpoint The ids endpoint.
     * @param accessURL   The access url of the ids endpoint.
     * @return True, if ids endpoint is updated.
     */
    private boolean updateAccessURL(final IdsEndpoint idsEndpoint, final URI accessURL) {
        final var newAccessUrl = MetadataUtils.updateUri(idsEndpoint.getAccessURL(),
                accessURL, DEFAULT_URI);
        newAccessUrl.ifPresent(idsEndpoint::setAccessURL);

        return newAccessUrl.isPresent();
    }

    /**
     * @param desc The description passed to the factory.
     * @return The new ids endpoint.
     */
    @Override
    protected IdsEndpoint createInternal(final IdsEndpointDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var idsEndpoint = new IdsEndpoint();

        update(idsEndpoint, desc);

        return idsEndpoint;
    }
}
