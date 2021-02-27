package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Map;

@Data
public class CatalogView extends RepresentationModel<CatalogView> {
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private String description;
    private Map<String, String> additional;
}
