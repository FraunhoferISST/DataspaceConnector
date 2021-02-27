package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Map;

@Data
public class RepresentationView extends RepresentationModel<RepresentationView> {
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private String mediaType;
    private String language;
    private Map<String, String> additional;
}
