package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.net.URL;

/**
 * A description of an artifact.
 * This class is consumed when creating or updating an artifact.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArtifactDesc extends AbstractDescription<Artifact> {

    /**
     * The agreement id on provider side.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteId;

    /**
     * The provider's address for artifact request messages.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteAddress;

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

    /**
     * Indicates whether the artifact should be downloaded automatically.
     */
    private boolean automatedDownload = ArtifactFactory.DEFAULT_AUTO_DOWNLOAD;
}
