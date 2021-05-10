package io.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

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
