package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import org.springframework.stereotype.Component;

@Component
public class ArtifactViewer implements BaseViewer<Artifact, ArtifactView> {
    @Override
    public ArtifactView create(final Artifact artifact) {
        final var view = new ArtifactView();
        view.setTitle(artifact.getTitle());

        return view;
    }
}
