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
package io.dataspaceconnector.model.appstore;

import java.net.URI;
import java.util.ArrayList;

import io.dataspaceconnector.model.AbstractNamedFactory;
import io.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates an app store.
 */
@Component
public class AppStoreFactory extends AbstractNamedFactory<AppStore, AppStoreDesc> {

    /**
     * The default uri.
     */
    private static final URI DEFAULT_LOCATION = URI.create("");

    /**
     * @param desc The description of the entity.
     * @return New app store entity.
     */
    @Override
    protected AppStore initializeEntity(final AppStoreDesc desc) {
        final var appStore = new AppStore();
        appStore.setAppList(new ArrayList<>());

        return appStore;
    }

    /**
     * @param appStore The entity to be updated.
     * @param desc     The description of the new entity.
     * @return True, if entity is updated.
     */
    @Override
    protected boolean updateInternal(final AppStore appStore, final AppStoreDesc desc) {
        return updateLocation(appStore, desc.getLocation());
    }

    /**
     * @param appStore  The entity to be updated.
     * @param location The new access url.
     * @return True, if access url is updated.
     */
    private boolean updateLocation(final AppStore appStore, final URI location) {
        final var newLocation = MetadataUtils.updateUri(appStore.getLocation(),
                                                         location, DEFAULT_LOCATION);
        newLocation.ifPresent(appStore::setLocation);
        return newLocation.isPresent();
    }
}
