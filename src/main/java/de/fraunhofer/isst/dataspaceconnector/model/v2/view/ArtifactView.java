package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import lombok.Data;

@Data
public class ArtifactView implements BaseView<Artifact>{
    private String title;
    private Long numAccessed;
}
