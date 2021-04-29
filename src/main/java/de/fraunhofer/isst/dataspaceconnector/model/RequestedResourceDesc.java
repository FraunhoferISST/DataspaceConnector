package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Describes a requested resource. Use this to create or
 * update an requested resource.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestedResourceDesc extends ResourceDesc<RequestedResource> {

    /**
     * The resource id on provider side.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteId;
}
