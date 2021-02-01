package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import lombok.Data;

import java.util.Set;

@Data
public class CatalogView implements BaseView<Catalog> {
    private String title;
    private String description;

    private Set<EndpointId> resources;
}
