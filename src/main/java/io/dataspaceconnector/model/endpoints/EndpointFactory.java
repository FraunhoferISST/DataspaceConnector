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
package io.dataspaceconnector.model.endpoints;

import java.net.URI;

import io.dataspaceconnector.model.base.AbstractFactory;

/**
 * Base class for creating and updating endpoints.
 *
 * @param <T> The endpoint type.
 * @param <D> The description type.
 */
public abstract class EndpointFactory<T extends Endpoint, D extends EndpointDesc<T>>
        extends AbstractFactory<T, D> {

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
//        Utils.requireNonNull(endpoint, ErrorMessages.ENTITY_NULL);
//        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);
//
//        final var hasUpdatedDocumentation = updateEndpointDocumentation(endpoint,
//                desc.getEndpointDocumentation());
//        final var hasUpdatedInformation = updateEndpointInformation(endpoint,
//                desc.getEndpointInformation());
//        final var hasUpdatedInboundPath = updateInboundPath(endpoint, desc.getInboundPath());
//        final var hasUpdatedOutboundPath = updateOutboundPath(endpoint, desc.getOutboundPath());
//        final var hasUpdatedAdditional = updateAdditional(endpoint, endpoint.getAdditional());
//
//        final var updatedInternal = updateInternal(endpoint, desc);
//
//        return hasUpdatedDocumentation || hasUpdatedInformation || hasUpdatedInboundPath
//                || hasUpdatedOutboundPath || hasUpdatedAdditional || updatedInternal;
        return false;
    }

//    /**
//     * @param endpoint     The endpoint entity.
//     * @param outboundPath The outbound path of the entity.
//     * @return True, if outbound path is updated.
//     */
//    private boolean updateOutboundPath(final Endpoint endpoint, final String outboundPath) {
//        final var newOutboundPath = MetadataUtils.updateString(endpoint.getOutboundPath(),
//                outboundPath, DEFAULT_OUTBOUND_PATH);
//        newOutboundPath.ifPresent(endpoint::setOutboundPath);
//
//        return newOutboundPath.isPresent();
//    }
//
//    /**
//     * @param endpoint    The endpoint entity.
//     * @param inboundPath The inbound path of the entity.
//     * @return True, if inbound path is updated.
//     */
//    private boolean updateInboundPath(final Endpoint endpoint, final String inboundPath) {
//        final var newInboundPath = MetadataUtils.updateString(endpoint.getInboundPath(),
//                inboundPath, DEFAULT_INBOUND_PATH);
//        newInboundPath.ifPresent(endpoint::setInboundPath);
//
//        return newInboundPath.isPresent();
//    }
//
//    /**
//     * @param endpoint            The endpoint entity.
//     * @param endpointInformation The endpoint information.
//     * @return True, if endpoint information is updated.
//     */
//    private boolean updateEndpointInformation(final Endpoint endpoint,
//                                              final String endpointInformation) {
//        final var newEndpointInfo =
//                MetadataUtils.updateString(endpoint.getEndpointInformation(), endpointInformation,
//                        DEFAULT_INFORMATION);
//        newEndpointInfo.ifPresent(endpoint::setEndpointInformation);
//
//        return newEndpointInfo.isPresent();
//    }
//
//
//    /**
//     * @param endpoint              The endpoint entity.
//     * @param endpointDocumentation The endpoint documentation.
//     * @return True, if endpoint documentation is updated.
//     */
//    private boolean updateEndpointDocumentation(final Endpoint endpoint,
//                                                final URI endpointDocumentation) {
//        final var newDocumentation =
//                MetadataUtils.updateUri(endpoint.getDocs(), endpointDocumentation,
//                                        DEFAULT_URI);
//        newDocumentation.ifPresent(endpoint::setDocs);
//
//        return newDocumentation.isPresent();
//    }
}
