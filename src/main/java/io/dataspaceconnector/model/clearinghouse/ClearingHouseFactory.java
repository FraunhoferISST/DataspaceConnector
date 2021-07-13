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
package io.dataspaceconnector.model.clearinghouse;

import java.net.URI;
import java.util.Objects;

import io.dataspaceconnector.model.AbstractNamedFactory;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.util.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates clearing houses.
 */
@Component
public class ClearingHouseFactory extends AbstractNamedFactory<ClearingHouse, ClearingHouseDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_LOCATION = URI.create("");

    /**
     * Initializes a clearing house entity.
     * @param desc not used in this constructor
     * @return newly created ClearingHouse object
     */
    @Override
    protected ClearingHouse initializeEntity(final ClearingHouseDesc desc) {
        return new ClearingHouse();
    }

    /**
     * Updates the clearing house location and status.
     * @param clearingHouse clearing house to update
     * @param desc clearing house description with the desired update
     * @return true if clearing house was successfully updated
     */
    @Override
    protected boolean updateInternal(final ClearingHouse clearingHouse,
                                     final ClearingHouseDesc desc) {
        final var hasUpdatedLocation = updateLocation(clearingHouse, desc.getLocation());
        final var newStatus = updateRegistrationStatus(clearingHouse, desc.getStatus());

        return hasUpdatedLocation || newStatus;
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param status        The registration status of the clearing house.
     * @return True, if clearing house is updated.
     */
    private boolean updateRegistrationStatus(final ClearingHouse clearingHouse,
                                             final RegistrationStatus status) {
        clearingHouse.setStatus(
                Objects.requireNonNullElse(status, RegistrationStatus.UNREGISTERED));
        return true;
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param location     The new access url of the entity.
     * @return True, if clearing house is updated.
     */
    private boolean updateLocation(final ClearingHouse clearingHouse, final URI location) {
        final var newLocation =
                MetadataUtils.updateUri(clearingHouse.getLocation(), location, DEFAULT_LOCATION);
        newLocation.ifPresent(clearingHouse::setLocation);
        return newLocation.isPresent();
    }
}
