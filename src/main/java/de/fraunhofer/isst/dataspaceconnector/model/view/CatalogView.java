package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class CatalogView extends RepresentationModel<CatalogView> {
    private String title;
    private String description;
}
