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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates an app store.
 */
@Component
public class AppStoreFactory implements AbstractFactory<AppStore, AppStoreDesc> {

    /**
     * The default uri.
     */
    private static final URI DEFAULT_LOCATION = URI.create("");
    /**
     * The default title.
     */
    private static final String DEFAULT_TITLE = "";

    /**
     * @param desc The description of the entity.
     * @return New app store entity.
     */
    @Override
    public AppStore create(final AppStoreDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var appStore = new AppStore();
        appStore.setAppList(new ArrayList<>());

        update(appStore, desc);

        return appStore;
    }

    /**
     * @param appStore The entity to be updated.
     * @param desc     The description of the new entity.
     * @return True, if entity is updated.
     */
    @Override
    public boolean update(final AppStore appStore, final AppStoreDesc desc) {
        Utils.requireNonNull(appStore, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedTitle = updateTitle(appStore, desc.getTitle());
        final var hasUpdatedName = updateName(appStore, desc.getName());
        final var hasUpdatedRegistrationStatus =
                updateRegistrationStatus(appStore, desc.getStatus());
        final var hasUpdatedAdditional = updateAdditional(appStore, desc.getAdditional());

        return hasUpdatedTitle || hasUpdatedName || hasUpdatedRegistrationStatus
                || hasUpdatedAdditional;
    }

    /**
     * @param appStore           The entity to be updated.
     * @param status The new registration status.
     * @return True after updating the registration status.
     */
    private boolean updateRegistrationStatus(final AppStore appStore,
                                             final RegistrationStatus status) {
        appStore.setStatus(
                Objects.requireNonNullElse(status, RegistrationStatus.UNREGISTERED));
        return true;
    }

    /**
     * @param appStore  The entity to be updated.
     * @param name The new access url.
     * @return True, if access url is updated.
     */
    private boolean updateName(final AppStore appStore, final URI name) {
        final var newName = MetadataUtils.updateUri(appStore.getName(),
                                                         name, DEFAULT_LOCATION);
        newName.ifPresent(appStore::setName);
        return newName.isPresent();
    }

    /**
     * @param appStore The entity to be updated.
     * @param title    The new title.
     * @return True, if title is updated.
     */
    private boolean updateTitle(final AppStore appStore, final String title) {
        final var newTitle = MetadataUtils.updateString(appStore.getTitle(), title,
                DEFAULT_TITLE);
        newTitle.ifPresent(appStore::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param appStore   The entity to be updated.
     * @param additional The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final AppStore appStore,
                                     final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                appStore.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(appStore::setAdditional);

        return newAdditional.isPresent();
    }
}
