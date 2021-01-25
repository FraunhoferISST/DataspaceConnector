package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

@Component
public class ArtifactFactory implements BaseFactory<Artifact, ArtifactDesc> {
    @Override
    public Artifact create(final ArtifactDesc desc) {
        var artifact = new Artifact();
        update(artifact, desc);

        return artifact;
    }

    @Override
    public boolean update(final Artifact artifact, final ArtifactDesc desc) {
        var hasBeenUpdated = false;

        var newTitle = desc.getTitle() != null ? desc.getTitle() : "";
        if (newTitle.equals(artifact.getTitle())) {
            artifact.setTitle(newTitle);
            hasBeenUpdated = true;
        }

        if (desc.getAccessUrl() != null
                && desc.getAccessUrl().toString().length() > 0) {
            var data = new RemoteData();
            data.setAccessUrl(desc.getAccessUrl());
            data.setUsername(desc.getUsername());
            data.setPassword(desc.getPassword());

            artifact.setData(data);
            hasBeenUpdated = true;
        } else {
            var data = new LocalData();
            data.setValue(desc.getValue());

            artifact.setData(data);
            hasBeenUpdated = true;
        }

        return hasBeenUpdated;
    }
}
