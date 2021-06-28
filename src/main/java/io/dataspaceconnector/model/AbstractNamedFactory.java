package io.dataspaceconnector.model;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.utils.MetadataUtils;

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

    @Override
    public boolean update(final T entity, final D desc)  {
        final var hasParentUpdated = super.update(entity, desc);
        final var hasTitleUpdated = updateTitle(entity, desc.getTitle());
        final var hasDescUpdated = updateDescription(entity, desc.getDescription());
        return hasParentUpdated || hasTitleUpdated || hasDescUpdated;
    }

    private boolean updateTitle(final T entity, final String title) {
        final var newTitle = MetadataUtils.updateString(entity.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(entity::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateDescription(final T entity, final String description) {
        final var newDescription =
                MetadataUtils.updateString(entity.getDescription(), description,
                                           DEFAULT_DESCRIPTION);
        newDescription.ifPresent(entity::setDescription);

        return newDescription.isPresent();
    }
}
