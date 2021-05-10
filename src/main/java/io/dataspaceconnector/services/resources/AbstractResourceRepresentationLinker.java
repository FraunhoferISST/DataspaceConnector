package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.Resource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles the relation between a resources and its representations.
 * @param <T> The resource type.
 */
@NoArgsConstructor
public abstract class AbstractResourceRepresentationLinker<T extends Resource>
        extends OwningRelationService<T, Representation, ResourceService<T, ?>,
                        RepresentationService> {
    /**
     * Get the list of representations owned by the resource.
     * @param owner The owner of the representations.
     * @return The list of owned representations.
     */
    @Override
    protected List<Representation> getInternal(final Resource owner) {
        return owner.getRepresentations();
    }
}

/**
 * Handles the relation between an offered resource and its representations.
 */
@Service
@NoArgsConstructor
class OfferedResourceRepresentation extends AbstractResourceRepresentationLinker<OfferedResource> {
}

/**
 * Handles the relation between a requested resource and its representations.
 */
@Service
@NoArgsConstructor
class RequestedResourceRepresentation
        extends AbstractResourceRepresentationLinker<RequestedResource> { }
