package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.services.utils.MetaDataUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Creates and updates a resource.
 */
@Component
public class ResourceFactory implements BaseFactory<Resource, ResourceDesc> {

    /**
     * Create a new resource.
     *
     * @param desc The description of the new resource.
     * @return The new resource.
     */
    @Override
    public Resource create(final ResourceDesc desc) {
        final var resource = new Resource();
        resource.setRepresentations(new HashMap<>());
        resource.setContracts(new HashMap<>());
        resource.setKeywords(new ArrayList<>());
        // Set to -1 the following update will increment it to 0
        resource.setVersion(-1);

        update(resource, desc);

        return resource;
    }

    /**
     * Update a resource.
     *
     * @param resource The resource to be updated.
     * @param desc     The new resource description.
     * @return True if the resource has been modified.
     */
    @Override
    public boolean update(final Resource resource, final ResourceDesc desc) {
        final var hasUpdatedTitle = this.updateTitle(resource,
                desc.getTitle());
        final var hasUpdatedDesc = this.updateDescription(resource,
                desc.getDescription());
        final var hasUpdatedKeywords = this.updateKeywords(resource,
                desc.getKeywords());
        final var hasUpdatedPublisher = this.updatePublisher(resource,
                desc.getPublisher());
        final var hasUpdatedLanguage = this.updateLanguage(resource,
                desc.getLanguage());
        final var hasUpdatedLicence = this.updateLicence(resource,
                desc.getLicence());

        final var hasUpdated = hasUpdatedTitle || hasUpdatedDesc
                || hasUpdatedKeywords || hasUpdatedPublisher
                || hasUpdatedLanguage || hasUpdatedLicence;

        if (hasUpdated) {
            resource.setVersion(resource.getVersion() + 1);
        }

        return hasUpdated;
    }

    private boolean updateTitle(final Resource resource,
                                final String title) {
        final var newTitle =
                MetaDataUtils.updateString(resource.getTitle(),
                        title, "");
        newTitle.ifPresent(resource::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateDescription(final Resource resource,
                                      final String description) {
        final var newDesc =
                MetaDataUtils.updateString(resource.getDescription(),
                        description, "");
        newDesc.ifPresent(resource::setDescription);

        return newDesc.isPresent();
    }

    private boolean updateKeywords(final Resource resource,
                                   final List<String> keywords) {
        final var newKeys =
                MetaDataUtils.updateStringList(resource.getKeywords(),
                        keywords, Collections.singletonList(""));
        newKeys.ifPresent(resource::setKeywords);

        return newKeys.isPresent();
    }

    private boolean updatePublisher(final Resource resource,
                                    final URI publisher) {
        final var newPublisher =
                MetaDataUtils.updateUri(resource.getPublisher(),
                        publisher, URI.create(""));
        newPublisher.ifPresent(resource::setPublisher);

        return newPublisher.isPresent();
    }

    private boolean updateLanguage(final Resource resource,
                                   final String language) {
        final var newLanguage =
                MetaDataUtils.updateString(resource.getLanguage(), language,
                        "");
        newLanguage.ifPresent(resource::setLanguage);

        return newLanguage.isPresent();
    }

    private boolean updateLicence(final Resource resource,
                                  final URI licence) {
        final var newLicence =
                MetaDataUtils.updateUri(resource.getLicence(),
                        licence, URI.create(""));
        newLicence.ifPresent(resource::setLicence);

        return newLicence.isPresent();
    }
}
