package io.dataspaceconnector.model.templates;

import io.dataspaceconnector.model.CatalogDesc;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.model.RequestedResourceDesc;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class CatalogTemplate {

    /**
     * Resource parameters.
     */
    @Setter(AccessLevel.NONE)
    private @NonNull CatalogDesc desc;

    /**
     * List of offered resource templates.
     */
    private List<ResourceTemplate<OfferedResourceDesc>> offeredResources;

    /**
     * List of requested resource templates.
     */
    private List<ResourceTemplate<RequestedResourceDesc>> requestedResources;

}
