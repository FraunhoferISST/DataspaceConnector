package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

@Data
@Embeddable
public class EndpointId implements Serializable {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The base path.
     */
    @JsonIgnore
    private String basePath;

    /**
     * The uuid.
     */
    @JsonIgnore
    private UUID resourceId;

    /**
     * Constructor without params.
     */
    public EndpointId() {
        // emtpy
    }

    /**
     * Constructor with params.
     *
     * @param basePath   The base path.
     * @param resourceId The uuid.
     */
    public EndpointId(final String basePath, final UUID resourceId) {
        this.basePath = basePath;
        this.resourceId = resourceId;
    }

    /**
     * Convert base path and uuid to uri.
     *
     * @return The built uri.
     */
    @JsonProperty("href")
    public URI toUri() {
        return URI.create(basePath + "/" + resourceId.toString());
    }

    /**
     * Build endpoint id from given uri.
     *
     * @param uri The uri.
     */
    @JsonProperty("href")
    public void setUri(final URI uri) {
        final var extractedId = EndpointUtils.getEndpointIdFromPath(uri);

        this.basePath = extractedId.getBasePath();
        this.resourceId = extractedId.getResourceId();
    }
}
