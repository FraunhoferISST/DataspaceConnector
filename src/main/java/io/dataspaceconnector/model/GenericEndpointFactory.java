package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates generic endpoints.
 */
@Component
public class GenericEndpointFactory extends EndpointFactory<GenericEndpoint, GenericEndpointDesc> {

    /**
     * Default absolute path.
     */
    private final static String DEFAULT_PATH = "https://path";

    /**
     * @param genericEndpoint The generic endpoint.
     * @param desc            The description of the new entity.
     * @return True, if generic endpoint is updated.
     */
    @Override
    public boolean update(final GenericEndpoint genericEndpoint, final GenericEndpointDesc desc) {
        Utils.requireNonNull(genericEndpoint, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        return updateAbsolutePath(genericEndpoint, desc.getAbsolutePath());
    }

    /**
     * @param genericEndpoint The generic endpoint.
     * @param absolutePath    The absolute path of the generic endpoint.
     * @return True, if generic endpoint is updated.
     */
    private boolean updateAbsolutePath(final GenericEndpoint genericEndpoint, final String absolutePath) {
        final var newAbsolutePath = MetadataUtils.updateString(genericEndpoint.getAbsolutePath(),
                absolutePath, DEFAULT_PATH);
        newAbsolutePath.ifPresent(genericEndpoint::setAbsolutePath);

        return newAbsolutePath.isPresent();
    }

    /**
     * @param desc The description passed to the factory.
     * @return The new generic endpoint entity.
     */
    @Override
    protected GenericEndpoint createInternal(final GenericEndpointDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var genericEndpoint = new GenericEndpoint();

        update(genericEndpoint, desc);

        return genericEndpoint;
    }
}
