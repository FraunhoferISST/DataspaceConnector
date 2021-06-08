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

import io.dataspaceconnector.utils.MetadataUtils;
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
    private static final URI DEFAULT_ACCESS_URL = URI.create("https://path");

    /**
     * @param desc The description passed to the factory.
     * @return The new ids endpoint.
     */
    @Override
    protected IdsEndpoint createInternal(final IdsEndpointDesc desc) {
        return new IdsEndpoint();
    }

    /**
     * @param idsEndpoint The ids endpoint.
     * @param desc        The description of the new entity.
     * @return True, if ids endpoint is updated.
     */
    @Override
    protected boolean updateInternal(final IdsEndpoint idsEndpoint, final IdsEndpointDesc desc) {
        return updateAccessURL(idsEndpoint, desc.getAccessURL());
    }

    /**
     * @param idsEndpoint The ids endpoint.
     * @param accessURL   The access url of the ids endpoint.
     * @return True, if ids endpoint is updated.
     */
    private boolean updateAccessURL(final IdsEndpoint idsEndpoint, final URI accessURL) {
        final var newAccessUrl = MetadataUtils.updateUri(idsEndpoint.getAccessURL(),
                accessURL, DEFAULT_ACCESS_URL);
        newAccessUrl.ifPresent(idsEndpoint::setAccessURL);

        return newAccessUrl.isPresent();
    }
}
