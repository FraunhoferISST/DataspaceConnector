package de.fraunhofer.isst.dataspaceconnector.model.view;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(collectionRelation = "catalogs", itemRelation = "catalog")
public class CatalogView extends RepresentationModel<CatalogView> {
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private String description;
    private Map<String, String> additional;
}
