package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Map;

@Data
public class ArtifactView  extends RepresentationModel<ArtifactView> {
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private Long numAccessed;
    private Map<String, String> additional;
}
