package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ArtifactView  extends RepresentationModel<ArtifactView> {
    private String title;
    private Long numAccessed;
}
