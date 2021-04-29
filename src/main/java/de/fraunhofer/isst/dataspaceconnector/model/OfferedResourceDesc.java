package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Describes an offered resource. Use this structure to create
 * or update an offered resource.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OfferedResourceDesc extends ResourceDesc<OfferedResource> {
}
