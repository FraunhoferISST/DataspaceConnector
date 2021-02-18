package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URL;

/**
 * A description of an artifact.
 * This class is consumed when creating or updating an artifact.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArtifactDesc extends BaseDescription<Artifact> {

    /**
     * The title of the artifact.
     */
    private String title;

    /**
     * The url of the data location.
     */
    private URL accessUrl;

    /**
     * The username for authentication at the data location.
     */
    private String username;

    /**
     * The password for authentication at the data location.
     */
    private String password;

    /**
     * Some value for storing data locally.
     */
    private String value;
}
