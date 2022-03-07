/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.model.endpoint;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

import java.net.URI;

/**
 * Base class for creating and updating endpoints.
 *
 * @param <T> The endpoint type.
 * @param <D> The description type.
 */
public abstract class EndpointFactory<T extends Endpoint, D extends EndpointDesc>
        extends AbstractFactory<T, D> {

    /**
     * The default uri.
     */
    public static final URI DEFAULT_URI = URI.create("https://documentation");

    /**
     * The default information.
     */
    public static final String DEFAULT_INFORMATION = "information";

    /**
     * The default location.
     */
    public static final String DEFAULT_LOCATION = "https://location";

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
        final var hasParentUpdated = super.update(endpoint, desc);
        final var hasUpdatedLocation = updateLocation(endpoint, desc.getLocation());
        final var hasUpdatedDocs = updateDocs(endpoint, desc.getDocs());
        final var hasUpdatedInfo = updateInfo(endpoint, desc.getInfo());

        final var updatedInternal = updateInternal(endpoint, desc);

        return hasParentUpdated || hasUpdatedLocation || hasUpdatedDocs || hasUpdatedInfo
                || updatedInternal;
    }

    /**
     * @param endpoint The endpoint entity.
     * @param info     The endpoint information.
     * @return True, if endpoint information is updated.
     */
    private boolean updateInfo(final Endpoint endpoint,
                               final String info) {
        final var newInfo =
                FactoryUtils.updateString(endpoint.getInfo(), info, DEFAULT_INFORMATION);
        newInfo.ifPresent(endpoint::setInfo);

        return newInfo.isPresent();
    }


    /**
     * @param endpoint The endpoint entity.
     * @param docs     The endpoint documentation.
     * @return True, if endpoint documentation is updated.
     */
    private boolean updateDocs(final Endpoint endpoint,
                               final URI docs) {
        final var newDocs = FactoryUtils.updateUri(endpoint.getDocs(), docs, DEFAULT_URI);
        newDocs.ifPresent(endpoint::setDocs);

        return newDocs.isPresent();
    }

    /**
     * @param endpoint The endpoint entity.
     * @param location The endpoint location.
     * @return True, if endpoint location is updated.
     */
    private boolean updateLocation(final Endpoint endpoint,
                                   final String location) {
        final var newLocation = FactoryUtils.updateString(endpoint.getLocation(),
                location, DEFAULT_LOCATION);
        newLocation.ifPresent(endpoint::setLocation);

        return newLocation.isPresent();
    }
}
