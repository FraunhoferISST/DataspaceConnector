package de.fraunhofer.isst.dataspaceconnector.services.ids.builder;

import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Converts DSC artifacts to ids artifacts.
 */
@Component
@RequiredArgsConstructor
public final class IdsCatalogBuilder extends AbstractIdsBuilder<Catalog, ResourceCatalog> {

    /**
     * The builder for ids resource (from offered resource).
     */
    private final @NonNull IdsResourceBuilder<OfferedResource> resourceBuilder;

    @Override
    protected ResourceCatalog createInternal(final Catalog catalog, final URI baseUri,
                                             final int currentDepth, final int maxDepth)
            throws ConstraintViolationException {
        // Build children.
        final var resources = create(resourceBuilder,
                catalog.getOfferedResources(), baseUri, currentDepth, maxDepth);
        final var builder = new ResourceCatalogBuilder(getAbsoluteSelfLink(catalog, baseUri));
        resources.ifPresent(builder::_offeredResource_);

        return builder.build();
    }
}
