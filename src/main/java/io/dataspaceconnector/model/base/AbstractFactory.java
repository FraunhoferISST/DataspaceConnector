package io.dataspaceconnector.model.base;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;

public abstract class AbstractFactory<T extends AbstractEntity, D extends Description> {

    protected abstract T initializeEntity(D desc);

    protected boolean updateInternal(final T entity, final D desc) { return false; }

    public T create(final D desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var entity = initializeEntity(desc);
        if (desc.getBootstrapId() != null) {
            entity.setBootstrapId(desc.getBootstrapId());
        }

        update(entity, desc);

        return entity;
    }

    public boolean update(final T entity, final D desc) {
        Utils.requireNonNull(entity, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        boolean bootstrapId;
        if (desc.getBootstrapId() != null) {
            bootstrapId = this.updateBootstrapId(entity, desc.getBootstrapId());
        } else {
            bootstrapId = false;
        }

        final var internal = updateInternal(entity, desc);
        final var additional = updateAdditional(entity, desc.getAdditional());

        return internal || additional || bootstrapId;
    }

    protected boolean updateAdditional(final T entity, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                entity.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(entity::setAdditional);

        return newAdditional.isPresent();
    }

    protected boolean updateBootstrapId(final T entity, final URI bootstrapId) {
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
