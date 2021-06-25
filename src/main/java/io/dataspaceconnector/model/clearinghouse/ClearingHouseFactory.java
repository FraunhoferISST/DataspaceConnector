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

import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates clearing houses.
 */
@Component
public class ClearingHouseFactory extends AbstractFactory<ClearingHouse, ClearingHouseDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_NAME = URI.create("");

    /**
     * Default string value.
     */
    private static final String DEFAULT_TITLE = "";

    @Override
    protected ClearingHouse initializeEntity(final ClearingHouseDesc desc) {
        return new ClearingHouse();
    }

    @Override
    protected boolean updateInternal(final ClearingHouse clearingHouse, final ClearingHouseDesc desc) {
        final var hasUpdatedName = updateName(clearingHouse, desc.getName());
        final var newTitle = updateTitle(clearingHouse, desc.getTitle());
        final var newStatus = updateRegistrationStatus(clearingHouse, desc.getStatus());

        return hasUpdatedName || newTitle || newStatus;
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
     * @param title         The new title of the entity.
     * @return True, if clearing house is updated
     */
    private boolean updateTitle(final ClearingHouse clearingHouse, final String title) {
        final var newTitle = MetadataUtils.updateString(clearingHouse.getTitle(),
                                                        title, DEFAULT_TITLE);
        newTitle.ifPresent(clearingHouse::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param name     The new access url of the entity.
     * @return True, if clearing house is updated.
     */
    private boolean updateName(final ClearingHouse clearingHouse, final URI name) {
        final var newAccessUrl =
                MetadataUtils.updateUri(clearingHouse.getName(), name, DEFAULT_NAME);
        newAccessUrl.ifPresent(clearingHouse::setName);
        return newAccessUrl.isPresent();
    }
}
