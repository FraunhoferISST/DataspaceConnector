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
 * Creates and updates identity providers.
 */
@Component
public class IdentityProviderFactory
        implements AbstractFactory<IdentityProvider, IdentityProviderDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_URI = URI.create("https://identityprovider.com");

    /**
     * Default string value.
     */
    private static final String DEFAULT_STRING = "unknown";

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

        final var newAccessUrl = updateAccessUrl(identityProvider,
                identityProvider.getAccessUrl());
        final var newTitle = updateTitle(identityProvider, identityProvider.getTitle());
        final var newStatus = updateRegisterStatus(identityProvider,
                identityProvider.getRegisterStatus());

        return newAccessUrl || newTitle || newStatus;
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param status           The registration status of the identity provider.
     * @return True, if registration status is updated.
     */
    private boolean updateRegisterStatus(final IdentityProvider identityProvider,
                                         final RegisterStatus status) {
        final boolean updated;
        if (identityProvider.getRegisterStatus().equals(status)) {
            updated = false;
        } else {
            identityProvider.setRegisterStatus(status);
            updated = true;
        }
        return updated;
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param title            The new title of the entity.
     * @return True, if title is updated.
     */
    private boolean updateTitle(final IdentityProvider identityProvider, final String title) {
        final var newTitle =
                MetadataUtils.updateString(identityProvider.getTitle(), title, DEFAULT_STRING);
        newTitle.ifPresent(identityProvider::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param identityProvider The entity to be updated.
     * @param accessUrl        The new access url of the entity.
     * @return True, if access url is updated.
     */
    private boolean updateAccessUrl(final IdentityProvider identityProvider, final URI accessUrl) {
        final var newAccessUrl =
                MetadataUtils.updateUri(identityProvider.getAccessUrl(), accessUrl, DEFAULT_URI);
        newAccessUrl.ifPresent(identityProvider::setAccessUrl);
        return newAccessUrl.isPresent();
    }
}
