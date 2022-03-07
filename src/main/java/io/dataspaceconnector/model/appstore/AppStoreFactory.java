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
package io.dataspaceconnector.model.appstore;

import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;

/**
 * Creates and updates an app store.
 */
@Component
public class AppStoreFactory extends AbstractNamedFactory<AppStore, AppStoreDesc> {

    /**
     * Default access url.
     */
    public static final URI DEFAULT_URI = URI.create("https://appstore.com");

    /**
     * Create a new AppStore from Desc.
     *
     * @param desc The description of the entity.
     * @return The new app store entity.
     */
    @Override
    protected AppStore initializeEntity(final AppStoreDesc desc) {
        final var appStore = new AppStore();
        appStore.setApps(new ArrayList<>());

        return appStore;
    }

    /**
     * Update the AppStore based on given AppDesc.
     *
     * @param appStore The entity to be updated.
     * @param desc     The description of the new entity.
     * @return True, if app store is updated.
     */
    @Override
    protected boolean updateInternal(final AppStore appStore, final AppStoreDesc desc) {
        return updateLocation(appStore, desc.getLocation());
    }

    /**
     * Update Location URL of the AppStore.
     *
     * @param appStore The entity to be updated.
     * @param location The new location url of the entity.
     * @return True, if app store is updated.
     */
    private boolean updateLocation(final AppStore appStore, final URI location) {
        final var newLocation = FactoryUtils.updateUri(appStore.getLocation(), location,
                DEFAULT_URI);
        newLocation.ifPresent(appStore::setLocation);

        return newLocation.isPresent();
    }
}
