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

import org.springframework.stereotype.Component;

/**
 * Creates and updates an app store.
 */
@Component
public class AppStoreFactory implements AbstractFactory<AppStore, AppStoreDesc> {

    /**
     * @param desc The description of the entity.
     * @return New app store entity.
     */
    @Override
    public AppStore create(final AppStoreDesc desc) {
        return new AppStore();
    }

    /**
     * @param entity The entity to be updated.
     * @param desc   The description of the new entity.
     * @return True, if entity is updated.
     */
    @Override
    public boolean update(final AppStore entity, final AppStoreDesc desc) {
        return false;
    }
}
