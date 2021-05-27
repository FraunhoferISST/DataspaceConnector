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
 * Creates and updates an app.
 */
@Component
public class AppFactory implements AbstractFactory<App, AppDesc> {

    /**
     * Creates an app.
     *
     * @param desc The description of the entity.
     * @return new app entity.
     */
    @Override
    public App create(final AppDesc desc) {
        return new App();
    }

    /**
     * Updates an app with the description.
     *
     * @param entity The entity to be updated.
     * @param desc   The description of the new entity.
     * @return true, if app is updated
     */
    @Override
    public boolean update(final App entity, final AppDesc desc) {
        return false;
    }
}
