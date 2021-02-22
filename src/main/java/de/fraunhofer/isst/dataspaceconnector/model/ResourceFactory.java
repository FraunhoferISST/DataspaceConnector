package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class ResourceFactory<T extends Resource, D extends ResourceDesc<T>>
        implements AbstractFactory<T, D> {

    /**
     * Default constructor.
     */
    public ResourceFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new resource.
     *
     * @param desc The description of the new resource.
     * @return The new resource.
     */
    @Override
    public T create(final D desc) {
        final var resource = createInternal(desc);
        resource.setRepresentations(new HashMap<>());
        resource.setContracts(new HashMap<>());
        resource.setKeywords(new ArrayList<>());
        // Set to -1 the following update will increment it to 0
        resource.setVersion(-1);

        update(resource, desc);

        return resource;
    }

    protected abstract T createInternal(final D desc);

    /**
     * Update a resource.
     *
     * @param resource The resource to be updated.
     * @param desc     The new resource description.
     * @return True if the resource has been modified.
     */
    @Override
    public boolean update(final T resource, final D desc) {
        final var hasUpdatedTitle = updateTitle(resource, desc.getTitle());
        final var hasUpdatedDesc = updateDescription(resource, desc.getDescription());
        final var hasUpdatedKeywords = updateKeywords(resource, desc.getKeywords());
        final var hasUpdatedPublisher = updatePublisher(resource, desc.getPublisher());
        final var hasUpdatedLanguage = updateLanguage(resource, desc.getLanguage());
        final var hasUpdatedLicence = updateLicence(resource, desc.getLicence());

        final var hasChildUpdated = updateInternal(resource, desc);

        final var hasUpdated = hasChildUpdated || hasUpdatedTitle || hasUpdatedDesc
                || hasUpdatedKeywords || hasUpdatedPublisher || hasUpdatedLanguage
                || hasUpdatedLicence;

        if (hasUpdated) {
            resource.setVersion(resource.getVersion() + 1);
        }

        return hasUpdated;
    }

    protected boolean updateInternal(final T resource, final D desc) {
        return false;
    }

    protected static boolean updateTitle(final Resource resource, final String title) {
        final var newTitle = MetadataUtils.updateString(resource.getTitle(), title, "");
        newTitle.ifPresent(resource::setTitle);

        return newTitle.isPresent();
    }

    protected static boolean updateDescription(final Resource resource, final String description) {
        final var newDesc = MetadataUtils.updateString(resource.getDescription(), description, "");
        newDesc.ifPresent(resource::setDescription);

        return newDesc.isPresent();
    }

    protected static boolean updateKeywords(final Resource resource, final List<String> keywords) {
        final var newKeys = MetadataUtils.updateStringList(
                resource.getKeywords(), keywords, Collections.singletonList(""));
        newKeys.ifPresent(resource::setKeywords);

        return newKeys.isPresent();
    }

    protected static boolean updatePublisher(final Resource resource, final URI publisher) {
        final var newPublisher =
                MetadataUtils.updateUri(resource.getPublisher(), publisher, URI.create(""));
        newPublisher.ifPresent(resource::setPublisher);

        return newPublisher.isPresent();
    }

    protected static boolean updateLanguage(final Resource resource, final String language) {
        final var newLanguage = MetadataUtils.updateString(resource.getLanguage(), language, "");
        newLanguage.ifPresent(resource::setLanguage);

        return newLanguage.isPresent();
    }

    protected static boolean updateLicence(final Resource resource, final URI licence) {
        final var newLicence =
                MetadataUtils.updateUri(resource.getLicence(), licence, URI.create(""));
        newLicence.ifPresent(resource::setLicence);

        return newLicence.isPresent();
    }
}
