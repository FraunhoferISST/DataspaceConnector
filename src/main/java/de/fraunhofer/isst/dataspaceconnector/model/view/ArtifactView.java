package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import lombok.Data;

@Data
public class ArtifactView implements BaseView<Artifact>{
    private String title;
    private Long numAccessed;
}
