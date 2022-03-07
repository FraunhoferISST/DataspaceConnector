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
package io.dataspaceconnector.model.named;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

/**
 * Abstract factory class which holds additional information like title, description.
 *
 * @param <T> The type of the entity.
 * @param <D> The type of the description.
 */
public abstract class AbstractNamedFactory<T extends NamedEntity, D extends NamedDescription>
        extends AbstractFactory<T, D> {

    /**
     * Default title assigned to all entities.
     */
    public static final String DEFAULT_TITLE = "";

    /**
     * Default description assigned to all entities.
     */
    public static final String DEFAULT_DESCRIPTION = "";

    /**
     * Updates an entity with a new given description.
     *
     * @param entity The entity.
     * @param desc   The description of the entity.
     * @return If updating the entity was successful.
     */
    @Override
    public boolean update(final T entity, final D desc) {
        final var hasParentUpdated = super.update(entity, desc);
        final var hasTitleUpdated = updateTitle(entity, desc.getTitle());
        final var hasDescUpdated = updateDescription(entity, desc.getDescription());
        return hasParentUpdated || hasTitleUpdated || hasDescUpdated;
    }

    private boolean updateTitle(final T entity, final String title) {
        final var newTitle = FactoryUtils.updateString(entity.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(entity::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateDescription(final T entity, final String description) {
        final var newDescription = FactoryUtils.updateString(entity.getDescription(), description,
                DEFAULT_DESCRIPTION);
        newDescription.ifPresent(entity::setDescription);

        return newDescription.isPresent();
    }
}
