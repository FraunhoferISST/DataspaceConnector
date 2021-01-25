package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;

import java.net.URI;

@Data
public class ArtifactDesc extends BaseDescription<Artifact> {
    private String title;

    private URI accessUrl;
    private String username;
    private String password;

    private String value;
}
