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
package io.dataspaceconnector.model;

import io.dataspaceconnector.model.base.Factory;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for creating and updating endpoints.
 *
 * @param <T> The endpoint type.
 * @param <D> The description type.
 */
public abstract class EndpointFactory<T extends Endpoint, D extends EndpointDesc<T>>
        implements Factory<T, D> {

    /**
     * The default uri.
     */
    private static final URI DEFAULT_URI = URI.create("https://documentation");
    /**
     * The default outbound path.
     */
    private static final String DEFAULT_OUTBOUND_PATH = "default";
    /**
     * The default inbound path.
     */
    private static final String DEFAULT_INBOUND_PATH = "default";
    /**
     * The default information.
     */
    private static final String DEFAULT_INFORMATION = "information";


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
     * Update an endpoint. Implement type specific stuff here.
     *
     * @param endpoint The endpoint to be updated.
     * @param desc     The description passed to the factory.
     * @return true if the endpoint has been modified.
     */
    protected boolean updateInternal(final T endpoint, final D desc) {
        return false;
    }

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
                desc.getEndpointInformation());
        final var hasUpdatedInboundPath = updateInboundPath(endpoint, desc.getInboundPath());
        final var hasUpdatedOutboundPath = updateOutboundPath(endpoint, desc.getOutboundPath());
        final var hasUpdatedAdditional = updateAdditional(endpoint, endpoint.getAdditional());

        final var updatedInternal = updateInternal(endpoint, desc);

        return hasUpdatedDocumentation || hasUpdatedInformation || hasUpdatedInboundPath
                || hasUpdatedOutboundPath || hasUpdatedAdditional || updatedInternal;
    }

    /**
     * @param endpoint     The endpoint entity.
     * @param outboundPath The outbound path of the entity.
     * @return True, if outbound path is updated.
     */
    private boolean updateOutboundPath(final Endpoint endpoint, final String outboundPath) {
        final var newOutboundPath = MetadataUtils.updateString(endpoint.getOutboundPath(),
                outboundPath, DEFAULT_OUTBOUND_PATH);
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
                inboundPath, DEFAULT_INBOUND_PATH);
        newInboundPath.ifPresent(endpoint::setInboundPath);

        return newInboundPath.isPresent();
    }

    /**
     * @param endpoint            The endpoint entity.
     * @param endpointInformation The endpoint information.
     * @return True, if endpoint information is updated.
     */
    private boolean updateEndpointInformation(final Endpoint endpoint,
                                              final String endpointInformation) {
        final var newEndpointInfo =
                MetadataUtils.updateString(endpoint.getEndpointInformation(), endpointInformation,
                        DEFAULT_INFORMATION);
        newEndpointInfo.ifPresent(endpoint::setEndpointInformation);

        return newEndpointInfo.isPresent();
    }


    /**
     * @param endpoint              The endpoint entity.
     * @param endpointDocumentation The endpoint documentation.
     * @return True, if endpoint documentation is updated.
     */
    private boolean updateEndpointDocumentation(final Endpoint endpoint,
                                                final URI endpointDocumentation) {
        final var newDocumentation =
                MetadataUtils.updateUri(endpoint.getEndpointDocumentation(), endpointDocumentation,
                        DEFAULT_URI);
        newDocumentation.ifPresent(endpoint::setEndpointDocumentation);

        return newDocumentation.isPresent();
    }

    /**
     * @param endpoint   The entity to be updated.
     * @param additional The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final Endpoint endpoint,
                                     final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                endpoint.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(endpoint::setAdditional);

        return newAdditional.isPresent();
    }
}
