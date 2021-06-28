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
        return updateName(appStore, desc.getName());
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
}
