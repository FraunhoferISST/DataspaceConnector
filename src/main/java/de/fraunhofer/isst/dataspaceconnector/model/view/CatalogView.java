package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import lombok.Data;

import java.util.Set;

@Data
public class CatalogView implements BaseView<Catalog> {
    private String title;
    private String description;

    private Set<EndpointId> resources;
}
