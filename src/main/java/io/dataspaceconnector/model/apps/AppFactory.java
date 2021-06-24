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
package io.dataspaceconnector.model.apps;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates an app.
 */
@Component
public class AppFactory extends AbstractFactory<App, AppDesc> {

    /**
     * The default title.
     */
    private static final String DEFAULT_TITLE = "";

    /**
     * Creates an app.
     *
     * @param desc The description of the entity.
     * @return new app entity.
     */
    @Override
    protected App initializeEntity(final AppDesc desc) {
        return new App();
    }

    /**
     * Updates an app with the description.
     *
     * @param app  The entity to be updated.
     * @param desc The description of the new entity.
     * @return true, if app is updated
     */
    @Override
    protected boolean updateInternal(final App app, final AppDesc desc) {
        return updateTitle(app, desc.getTitle());
    }

    /**
     * @param app   The entity to be updated.
     * @param title The new title.
     * @return True, if title is updated.
     */
    private boolean updateTitle(final App app, final String title) {
        final var newTitle = MetadataUtils.updateString(app.getTitle(), title,
                DEFAULT_TITLE);
        newTitle.ifPresent(app::setTitle);

        return newTitle.isPresent();
    }
}
