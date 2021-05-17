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

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
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
