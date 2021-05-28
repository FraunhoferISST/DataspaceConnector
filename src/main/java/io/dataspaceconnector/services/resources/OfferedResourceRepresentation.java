package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.OfferedResource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the relation between an offered resource and its representations.
 */
@Service
@NoArgsConstructor
public class OfferedResourceRepresentation
        extends AbstractResourceRepresentationLinker<OfferedResource> { }
