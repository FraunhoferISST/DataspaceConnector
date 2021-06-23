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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates identity providers.
 */
@Component
public class IdentityProviderFactory
        implements AbstractFactory<IdentityProvider, IdentityProviderDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_IDENTITY_PROVIDER = URI.create("");

    /**
     * Default string value.
     */
    private static final String DEFAULT_TITLE = "";

    /**
     * @param desc The description of the entity.
     * @return The new identity provider entity.
     */
    @Override
    public IdentityProvider create(final IdentityProviderDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var identityProvider = new IdentityProvider();

        update(identityProvider, desc);

        return identityProvider;
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param desc             The description of the new entity.
     * @return True, if entity is updated.
     */
    @Override
    public boolean update(final IdentityProvider identityProvider,
                          final IdentityProviderDesc desc) {
        Utils.requireNonNull(identityProvider, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedName = updateName(identityProvider, desc.getName());
        final var newTitle = updateTitle(identityProvider, desc.getTitle());
        final var newStatus = updateRegistrationStatus(identityProvider, desc.getStatus());
        final var newAdditional = updateAdditional(identityProvider, desc.getAdditional());

        return hasUpdatedName || newTitle || newStatus || newAdditional;
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param status           The registration status of the identity provider.
     * @return True, if registration status is updated.
     */
    private boolean updateRegistrationStatus(final IdentityProvider identityProvider,
                                             final RegistrationStatus status) {
        identityProvider.setStatus(
                Objects.requireNonNullElse(status, RegistrationStatus.UNREGISTERED));
        return true;
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param title            The new title of the entity.
     * @return True, if title is updated.
     */
    private boolean updateTitle(final IdentityProvider identityProvider, final String title) {
        final var newTitle =
                MetadataUtils.updateString(identityProvider.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(identityProvider::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param name        The new access url of the entity.
     * @return True, if access url is updated.
     */
    private boolean updateName(final IdentityProvider identityProvider, final URI name) {
        final var newLocation =
                MetadataUtils.updateUri(identityProvider.getName(), name,
                                        DEFAULT_IDENTITY_PROVIDER);
        newLocation.ifPresent(identityProvider::setName);
        return newLocation.isPresent();
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param additional       The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final IdentityProvider identityProvider,
                                     final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                identityProvider.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(identityProvider::setAdditional);

        return newAdditional.isPresent();
    }
}
