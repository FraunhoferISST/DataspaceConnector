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

import java.util.HashMap;
import java.util.Map;

import io.dataspaceconnector.model.base.Factory;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates an app.
 */
@Component
public class AppFactory implements Factory<App, AppDesc> {

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
    public App create(final AppDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var app = new App();

        update(app, desc);

        return app;
    }

    /**
     * Updates an app with the description.
     *
     * @param app  The entity to be updated.
     * @param desc The description of the new entity.
     * @return true, if app is updated
     */
    @Override
    public boolean update(final App app, final AppDesc desc) {
        Utils.requireNonNull(app, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedTitle = updateTitle(app, desc.getTitle());
        final var hasUpdatedAdditional = updateAdditional(app, desc.getAdditional());

        return hasUpdatedTitle || hasUpdatedAdditional;
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

    /**
     * @param app        The entity to be updated.
     * @param additional The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final App app, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                app.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(app::setAdditional);

        return newAdditional.isPresent();
    }
}
