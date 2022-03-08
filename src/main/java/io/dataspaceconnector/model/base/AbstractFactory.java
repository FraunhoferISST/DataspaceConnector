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
package io.dataspaceconnector.model.base;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.util.FactoryUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract factory class for entities.
 *
 * @param <T> The type of the entity.
 * @param <D> The type of the entity description.
 */
public abstract class AbstractFactory<T extends Entity, D extends Description> {

    protected abstract T initializeEntity(D desc);

    /**
     * Updates the internal entity representation with a given new description.
     *
     * @param entity The entity to update.
     * @param desc   The new description.
     * @return If updating the entity was successful.
     */
    protected boolean updateInternal(final T entity, final D desc) {
        return false;
    }

    /**
     * @param desc The description of the entity.
     * @return entity
     */
    public T create(final D desc) {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var entity = initializeEntity(desc);

        update(entity, desc);

        return entity;
    }

    /**
     * @param entity The entity.
     * @param desc   The description of the entity.
     * @return true, if entity is updated.
     */
    public boolean update(final T entity, final D desc) {
        Utils.requireNonNull(entity, ErrorMessage.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var additional = updateAdditional(entity, desc.getAdditional());
        final var bootstrap = updateBootstrapId(entity, desc.getBootstrapId());
        final var internal = updateInternal(entity, desc);

        return additional || bootstrap || internal;
    }

    protected final boolean updateAdditional(final T entity, final Map<String, String> additional) {
        final var newAdditional = FactoryUtils.updateStringMap(
                entity.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(entity::setAdditional);

        return newAdditional.isPresent();
    }

    protected final boolean updateBootstrapId(final T entity, final URI bootstrapId) {
        if (entity.getBootstrapId() == null && bootstrapId == null) {
            return false;
        }

        Optional<URI> newBootstrapId;
        if (bootstrapId == null && entity.getBootstrapId() == null) {
            newBootstrapId = Optional.empty();
        } else {
            newBootstrapId = FactoryUtils
                    .updateUri(
                            entity.getBootstrapId(),
                            bootstrapId,
                            entity.getBootstrapId());
        }

        newBootstrapId.ifPresent(entity::setBootstrapId);

        return newBootstrapId.isPresent();
    }
}
