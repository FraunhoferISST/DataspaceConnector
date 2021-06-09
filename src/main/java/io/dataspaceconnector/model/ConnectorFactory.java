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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Creates and updates a connector.
 */
@Component
public class ConnectorFactory implements AbstractFactory<Connector, ConnectorDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_ACCESS_URI = URI.create("https://localhost:8080");

    /**
     * Default string value.
     */
    private static final String DEFAULT_TITLE = "unknown";

    /**
     * @param desc The description of the entity.
     * @return The new connector entity.
     */
    @Override
    public Connector create(final ConnectorDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.MESSAGE_NULL);

        final var connector = new Connector();

        update(connector, desc);

        return connector;
    }

    /**
     * @param connector The entity to be updated.
     * @param desc      The description of the new entity.
     * @return True, if connector is updated.
     */
    @Override
    public boolean update(final Connector connector, final ConnectorDesc desc) {
        Utils.requireNonNull(connector, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var newAccessUrl = updateAccessUrl(connector, desc.getAccessUrl());
        final var newTitle = updateTitle(connector, desc.getTitle());
        final var newStatus = updateRegistrationStatus(connector,
                desc.getRegistrationStatus());
        final var newAdditional = updateAdditional(connector, desc.getAdditional());

        return newAccessUrl || newTitle || newStatus || newAdditional;
    }

    /**
     * @param connector The entity to be updated.
     * @param status    The registration status of the connector.
     * @return True, if connector is updated.
     */
    private boolean updateRegistrationStatus(final Connector connector,
                                             final RegistrationStatus status) {
        connector.setRegistrationStatus(
                Objects.requireNonNullElse(status, RegistrationStatus.UNREGISTERED));
        return true;
    }

    /**
     * @param connector The entity to be updated.
     * @param title     The new title of the entity.
     * @return True, if connector is updated.
     */
    private boolean updateTitle(final Connector connector, final String title) {
        final var newTitle = MetadataUtils.updateString(connector.getTitle(), title,
                DEFAULT_TITLE);
        newTitle.ifPresent(connector::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param connector The entity to be updated.
     * @param accessUrl The new access url of the entity.
     * @return True, if connector is updated.
     */
    private boolean updateAccessUrl(final Connector connector, final URI accessUrl) {
        final var newAccessUrl = MetadataUtils.updateUri(connector.getAccessUrl(), accessUrl,
                DEFAULT_ACCESS_URI);
        newAccessUrl.ifPresent(connector::setAccessUrl);
        return newAccessUrl.isPresent();
    }

    /**
     * @param connector  The entity to be updated.
     * @param additional The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final Connector connector,
                                     final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                connector.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(connector::setAdditional);

        return newAdditional.isPresent();
    }
}
