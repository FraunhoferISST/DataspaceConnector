package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArtifactDesc extends BaseDescription<Artifact> {
    private String title;

    private URI accessUrl;
    private String username;
    private String password;

    private String value;
}
