package io.dataspaceconnector.model.templates;

import io.dataspaceconnector.model.AbstractDescription;
import lombok.*;

import java.net.URI;
import java.util.List;

/**
 * Describes a resource and all its dependencies.
 * @param <D> The resource type.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ResourceTemplate<D extends AbstractDescription<?>> {

    /**
     * Old remote id.
     */
    private URI oldRemoteId;

    /**
     * Resource parameters.
     */
    @Setter(AccessLevel.NONE)
    private @NonNull D desc;

    /**
     * List of representation templates.
     */
    private List<RepresentationTemplate> representations;

    /**
     * List of contract templates.
     */
    private List<ContractTemplate> contracts;
}
