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
