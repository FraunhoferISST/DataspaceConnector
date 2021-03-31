package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;

public abstract class ResourceFactory<T extends Resource, D extends ResourceDesc<T>>
        implements AbstractFactory<T, D> {
    static final String DEFAULT_TITLE = "";
    static final String DEFAULT_DESCRIPTION = "";
    static final List<String> DEFAULT_KEYWORDS = new ArrayList(List.of("DSC"));
    static final URI DEFAULT_PUBLISHER = URI.create("");
    static final String DEFAULT_LANGUAGE = "";
    static final URI DEFAULT_LICENCE = URI.create("");
    static final URI DEFAULT_SOVEREIGN = URI.create("");
    static final URI DEFAULT_ENDPOINT_DOCS = URI.create("");

    /**
     * Default constructor.
     */
    public ResourceFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new resource.
     * @param desc The description of the new resource.
     * @return The new resource.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public T create(final D desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var resource = createInternal(desc);
        resource.setRepresentations(new ArrayList<>());
        resource.setContracts(new ArrayList<>());
        resource.setCatalogs(new ArrayList<>());

        update(resource, desc);

        return resource;
    }

    protected abstract T createInternal(D desc);

    /**
     * Update a resource.
     * @param resource The resource to be updated.
     * @param desc     The new resource description.
     * @return True if the resource has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    public boolean update(final T resource, final D desc) {
        Utils.requireNonNull(resource, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedTitle = updateTitle(resource, desc.getTitle());
        final var hasUpdatedDesc = updateDescription(resource, desc.getDescription());
        final var hasUpdatedKeywords = updateKeywords(resource, desc.getKeywords());
        final var hasUpdatedPublisher = updatePublisher(resource, desc.getPublisher());
        final var hasUpdatedLanguage = updateLanguage(resource, desc.getLanguage());
        final var hasUpdatedLicence = updateLicence(resource, desc.getLicence());
        final var hasUpdatedSovereign = updateSovereign(resource, desc.getSovereign());
        final var hasUpdatedEndpointDocs =
                updateEndpointDocs(resource, desc.getEndpointDocumentation());

        final var hasChildUpdated = updateInternal(resource, desc);

        final var hasUpdatedAdditional = updateAdditional(resource, desc.getAdditional());

        final var hasUpdated = hasChildUpdated || hasUpdatedTitle || hasUpdatedDesc
                || hasUpdatedKeywords || hasUpdatedPublisher || hasUpdatedLanguage
                || hasUpdatedLicence || hasUpdatedSovereign || hasUpdatedEndpointDocs
                || hasUpdatedAdditional;

        if (hasUpdated) {
            resource.setVersion(resource.getVersion() + 1);
        }

        return hasUpdated;
    }

    protected boolean updateInternal(final T resource, final D desc) {
        return false;
    }

    protected static boolean updateTitle(final Resource resource, final String title) {
        final var newTitle = MetadataUtils.updateString(resource.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(resource::setTitle);

        return newTitle.isPresent();
    }

    protected static boolean updateDescription(final Resource resource, final String description) {
        final var newDesc = MetadataUtils.updateString(
                resource.getDescription(), description, DEFAULT_DESCRIPTION);
        newDesc.ifPresent(resource::setDescription);

        return newDesc.isPresent();
    }

    protected static boolean updateKeywords(final Resource resource, final List<String> keywords) {
        final var newKeys =
                MetadataUtils.updateStringList(resource.getKeywords(), keywords, DEFAULT_KEYWORDS);
        newKeys.ifPresent(resource::setKeywords);

        return newKeys.isPresent();
    }

    protected static boolean updatePublisher(final Resource resource, final URI publisher) {
        final var newPublisher =
                MetadataUtils.updateUri(resource.getPublisher(), publisher, DEFAULT_PUBLISHER);
        newPublisher.ifPresent(resource::setPublisher);

        return newPublisher.isPresent();
    }

    protected static boolean updateLanguage(final Resource resource, final String language) {
        final var newLanguage =
                MetadataUtils.updateString(resource.getLanguage(), language, DEFAULT_LANGUAGE);
        newLanguage.ifPresent(resource::setLanguage);

        return newLanguage.isPresent();
    }

    protected static boolean updateLicence(final Resource resource, final URI licence) {
        final var newLicence =
                MetadataUtils.updateUri(resource.getLicence(), licence, DEFAULT_LICENCE);
        newLicence.ifPresent(resource::setLicence);

        return newLicence.isPresent();
    }

    protected static boolean updateSovereign(final Resource resource, final URI sovereign) {
        final var newPublisher =
                MetadataUtils.updateUri(resource.getSovereign(), sovereign, DEFAULT_SOVEREIGN);
        newPublisher.ifPresent(resource::setSovereign);

        return newPublisher.isPresent();
    }

    protected static boolean updateEndpointDocs(final Resource resource, final URI endpointDocs) {
        final var newPublisher = MetadataUtils.updateUri(
                resource.getEndpointDocumentation(), endpointDocs, DEFAULT_ENDPOINT_DOCS);
        newPublisher.ifPresent(resource::setEndpointDocumentation);

        return newPublisher.isPresent();
    }

    private boolean updateAdditional(
            final Resource resource, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                resource.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(resource::setAdditional);

        return newAdditional.isPresent();
    }
}
