package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.services.utils.MetaDataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a representation.
 */
@Component
public class RepresentationFactory implements BaseFactory<Representation,
        RepresentationDesc> {

    /**
     * Create a new representation.
     *
     * @param desc The description of the new representation.
     * @return The new representation.
     */
    @Override
    public Representation create(final RepresentationDesc desc) {
        final var representation = new Representation();
        update(representation, desc);

        return representation;
    }

    /**
     * Update a representation.
     *
     * @param representation The representation to be updated.
     * @param desc           The new representation description.
     * @return True if the representation has been modified.
     */
    @Override
    public boolean update(final Representation representation,
                          final RepresentationDesc desc) {
        final var hasUpdatedTitle = this.updateTitle(representation,
                desc.getTitle());
        final var hasUpdatedLanguage = this.updateLanguage(representation,
                desc.getLanguage());
        final var hasUpdatedMediaType = this.updateMediaType(representation,
                representation.getMediaType());

        return hasUpdatedTitle || hasUpdatedLanguage || hasUpdatedMediaType;
    }

    private boolean updateTitle(final Representation representation,
                                final String title) {
        final var newTitle =
                MetaDataUtils.updateString(representation.getTitle(),
                title, "");
        newTitle.ifPresent(representation::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateLanguage(final Representation representation,
                                   final String language) {
        final var newLanguage =
                MetaDataUtils.updateString(representation.getLanguage(),
                language, "");
        newLanguage.ifPresent(representation::setLanguage);

        return newLanguage.isPresent();
    }

    private boolean updateMediaType(final Representation representation,
                                    final String mediaType) {
        final var newMediaType =
                MetaDataUtils.updateString(representation.getMediaType(),
                mediaType, "");
        newMediaType.ifPresent(representation::setMediaType);

        return newMediaType.isPresent();
    }
}
