package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public abstract class EndpointFactory<T extends Endpoint, D extends EndpointDesc<T>>
        implements AbstractFactory<T, D> {

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

    @Override
    public boolean update(Endpoint entity, EndpointDesc desc) {
        return false;
    }
}
