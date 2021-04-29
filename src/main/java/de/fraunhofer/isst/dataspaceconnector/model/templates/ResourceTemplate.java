package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;
import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractDescription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
