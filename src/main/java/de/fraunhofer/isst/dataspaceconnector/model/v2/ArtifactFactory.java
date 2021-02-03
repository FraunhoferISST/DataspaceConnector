package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

/**
 * This class creates and updates Artifact.
 */
@Component
public final class ArtifactFactory implements BaseFactory<Artifact,
        ArtifactDesc> {

    /**
     * Default constructor.
     */
    private ArtifactFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    @Override
    public Artifact create(final ArtifactDesc desc) {
        final var artifact = new Artifact();
        update(artifact, desc);

        return artifact;
    }

    @Override
    public boolean update(final Artifact artifact, final ArtifactDesc desc) {
        final var updatedTitle = updateTitle(artifact, desc.getTitle());
        final var updatedData = updateData(artifact, desc);

        return updatedTitle || updatedData;
    }

    private boolean updateTitle(final Artifact artifact, final String title) {
        final var newTitle = title != null ? title : "";
        final var updateTitle = !newTitle.equals(artifact.getTitle());
        if (updateTitle) {
            artifact.setTitle(newTitle);
        }

        return updateTitle;
    }

    private static boolean updateData(final Artifact artifact,
                                      final ArtifactDesc desc) {
        final var isRemoteData = desc.getAccessUrl() != null
                && desc.getAccessUrl().toString().length() > 0;

        // TODO : Check if the data is really updated.
        if (isRemoteData) {
            final var data = new RemoteData();
            data.setAccessUrl(desc.getAccessUrl());
            data.setUsername(desc.getUsername());
            data.setPassword(desc.getPassword());

            artifact.setData(data);
            return true;
        } else {
            final var data = new LocalData();
            data.setValue(desc.getValue());

            artifact.setData(data);
            return true;
        }
    }
}
