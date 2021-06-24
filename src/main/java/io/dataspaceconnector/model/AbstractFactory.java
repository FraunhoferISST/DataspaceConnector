package io.dataspaceconnector.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.dataspaceconnector.model.base.Factory;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;

public abstract class AbstractFactory<T extends AbstractEntity, D extends AbstractDescription<T>> implements Factory<T, D> {

    abstract boolean updateInternal(T entity, D desc);

    @Override
    public boolean update(final T entity, final D desc) {
        Utils.requireNonNull(entity, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final boolean hasUpdatedBootstrapId;
        if (desc.getBootstrapId() != null) {
            hasUpdatedBootstrapId =
                    this.updateBootstrapId(entity, desc.getBootstrapId());
        } else {
            hasUpdatedBootstrapId = false;
        }

        return updateAdditional(entity, desc.getAdditional()) || hasUpdatedBootstrapId;
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
