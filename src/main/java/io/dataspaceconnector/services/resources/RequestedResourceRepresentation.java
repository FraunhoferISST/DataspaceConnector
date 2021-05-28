package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.RequestedResource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the relation between a requested resource and its representations.
 */
@Service
@NoArgsConstructor
public class RequestedResourceRepresentation
        extends AbstractResourceRepresentationLinker<RequestedResource> { }
