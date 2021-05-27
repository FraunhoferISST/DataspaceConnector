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

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Creates and updates clearing houses.
 */
@Component
public class ClearingHouseFactory implements AbstractFactory<ClearingHouse, ClearingHouseDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_URI = URI.create("https://clearinghouse.com");

    /**
     * Default string value.
     */
    private static final String DEFAULT_STRING = "unknown";

    /**
     * @param desc The description of the entity.
     * @return The new clearing house entity.
     */
    @Override
    public ClearingHouse create(final ClearingHouseDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var clearingHouse = new ClearingHouse();

        update(clearingHouse, desc);

        return clearingHouse;
    }

    /**
     * @param clearingHouse The clearing house entity.
     * @param desc          The description of the new entity.
     * @return True, if clearing house is updated.
     */
    @Override
    public boolean update(final ClearingHouse clearingHouse, final ClearingHouseDesc desc) {
        Utils.requireNonNull(clearingHouse, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var newAccessUrl = updateAccessUrl(clearingHouse,
                clearingHouse.getAccessUrl());
        final var newTitle = updateTitle(clearingHouse, clearingHouse.getTitle());
        final var newStatus = updateRegisterStatus(clearingHouse,
                clearingHouse.getRegisterStatus());

        return newAccessUrl || newTitle || newStatus;
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param status        The registration status of the clearing house.
     * @return True, if clearing house is updated.
     */
    private boolean updateRegisterStatus(final ClearingHouse clearingHouse,
                                         final RegisterStatus status) {
        final boolean updated;
        if (clearingHouse.getRegisterStatus().equals(status)) {
            updated = false;
        } else {
            clearingHouse.setRegisterStatus(status);
            updated = true;
        }
        return updated;
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param title         The new title of the entity.
     * @return True, if clearing house is updated
     */
    private boolean updateTitle(final ClearingHouse clearingHouse, final String title) {
        final var newTitle = MetadataUtils.updateString(clearingHouse.getTitle(),
                title, DEFAULT_STRING);
        newTitle.ifPresent(clearingHouse::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param accessUrl     The new access url of the entity.
     * @return True, if clearing house is updated.
     */
    private boolean updateAccessUrl(final ClearingHouse clearingHouse, final URI accessUrl) {
        final var newAccessUrl =
                MetadataUtils.updateUri(clearingHouse.getAccessUrl(), accessUrl, DEFAULT_URI);
        newAccessUrl.ifPresent(clearingHouse::setAccessUrl);
        return newAccessUrl.isPresent();
    }
}
