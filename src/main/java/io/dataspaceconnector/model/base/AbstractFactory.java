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
package io.dataspaceconnector.model.base;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;

public abstract class AbstractFactory<T extends Entity, D extends Description> {

    protected abstract T initializeEntity(D desc);

    protected boolean updateInternal(final T entity, final D desc) { return false; }

    public T create(final D desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var entity = initializeEntity(desc);
        initializeBootstrapId(entity, desc);

        update(entity, desc);

        return entity;
    }

    private void initializeBootstrapId(final T entity, final D desc) {
        if (desc.getBootstrapId() != null) {
            entity.setBootstrapId(desc.getBootstrapId());
        }
    }

    public boolean update(final T entity, final D desc) {
        Utils.requireNonNull(entity, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var additional = updateAdditional(entity, desc.getAdditional());
        final var bootstrap = updateBootstrapId(entity, desc.getBootstrapId());
        final var internal = updateInternal(entity, desc);

        return additional || bootstrap || internal;
    }

    protected boolean updateAdditional(final T entity, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                entity.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(entity::setAdditional);

        return newAdditional.isPresent();
    }

    protected boolean updateBootstrapId(final T entity, final URI bootstrapId) {
        // TODO Fix me
        if (bootstrapId == null) {
            return false;
        }

        Optional<URI> newBootstrapId;
        if (bootstrapId == null && entity.getBootstrapId() == null) {
            newBootstrapId = Optional.empty();
        } else {
            newBootstrapId = MetadataUtils
                    .updateUri(
                            entity.getBootstrapId(),
                            bootstrapId,
                            entity.getBootstrapId());
        }

        newBootstrapId.ifPresent(entity::setBootstrapId);

        return newBootstrapId.isPresent();
    }
}
