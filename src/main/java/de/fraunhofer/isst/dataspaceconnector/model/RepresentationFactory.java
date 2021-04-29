package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates and updates a representation.
 */
@Component
public class RepresentationFactory implements AbstractFactory<Representation, RepresentationDesc> {

    /**
     * The default remote id assigned to all representations.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * The default title assigned to all representations.
     */
    public static final String DEFAULT_TITLE = "";

    /**
     * The default language assigned to all representations.
     */
    public static final String DEFAULT_LANGUAGE = "EN";

    /**
     * The default media type assigned to all representations.
     */
    public static final String DEFAULT_MEDIA_TYPE = "";

    /**
     * The default standard assigned to all representations.
     */
    public static final String DEFAULT_STANDARD = "";

    /**
     * Create a new representation.
     * @param desc The description of the new representation.
     * @return The new representation.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public Representation create(final RepresentationDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var representation = new Representation();
        representation.setArtifacts(new ArrayList<>());
        representation.setResources(new ArrayList<>());

        update(representation, desc);

        return representation;
    }

    /**
     * Update a representation.
     * @param representation The representation to be updated.
     * @param desc           The new representation description.
     * @return True if the representation has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    public boolean update(final Representation representation, final RepresentationDesc desc) {
        Utils.requireNonNull(representation, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedRemoteId = this.updateRemoteId(representation, desc.getRemoteId());
        final var hasUpdatedTitle = this.updateTitle(representation, desc.getTitle());
        final var hasUpdatedMediaType = this.updateMediaType(representation, desc.getMediaType());
        final var hasUpdatedLanguage = this.updateLanguage(representation, desc.getLanguage());
        final var hasUpdatedStandard = this.updateStandard(representation, desc.getStandard());
        final var hasUpdatedAdditional =
                this.updateAdditional(representation, desc.getAdditional());

        return hasUpdatedRemoteId || hasUpdatedTitle || hasUpdatedLanguage || hasUpdatedMediaType
               || hasUpdatedStandard || hasUpdatedAdditional;
    }

    private boolean updateRemoteId(final Representation representation, final URI remoteId) {
        final var newUri = MetadataUtils.updateUri(
                representation.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(representation::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateTitle(final Representation representation, final String title) {
        final var newTitle =
                MetadataUtils.updateString(representation.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(representation::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateLanguage(final Representation representation, final String language) {
        final var newLanguage =
                MetadataUtils
                        .updateString(representation.getLanguage(), language, DEFAULT_LANGUAGE);
        newLanguage.ifPresent(representation::setLanguage);

        return newLanguage.isPresent();
    }

    private boolean updateMediaType(final Representation representation, final String mediaType) {
        final var newMediaType =
                MetadataUtils
                        .updateString(representation.getMediaType(), mediaType, DEFAULT_MEDIA_TYPE);
        newMediaType.ifPresent(representation::setMediaType);

        return newMediaType.isPresent();
    }

    private boolean updateStandard(final Representation representation, final String standard) {
        final var newAdditional =
                MetadataUtils
                        .updateString(representation.getStandard(), standard, DEFAULT_STANDARD);
        newAdditional.ifPresent(representation::setStandard);

        return newAdditional.isPresent();
    }

    private boolean updateAdditional(
            final Representation representation, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                representation.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(representation::setAdditional);

        return newAdditional.isPresent();
    }
}
