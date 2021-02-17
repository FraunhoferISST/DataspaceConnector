package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.springframework.stereotype.Component;

@Component
public class ArtifactViewFactory implements BaseViewFactory<Artifact, ArtifactView> {
    @Override
    public ArtifactView create(final Artifact artifact) {
        final var view = new ArtifactView();
        view.setTitle(artifact.getTitle());
        view.setNumAccessed(artifact.getNumAccessed());

        return view;
    }
}
