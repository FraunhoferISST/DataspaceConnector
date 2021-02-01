package de.fraunhofer.isst.dataspaceconnector.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

@Data
@Embeddable
public class EndpointId implements Serializable {
    @JsonIgnore
    private String basePath;

    @JsonIgnore
    private UUID resourceId;

    public EndpointId() {
    }

    public EndpointId(final String basePath, final UUID resourceId) {
        this.basePath = basePath;
        this.resourceId = resourceId;
    }

    @JsonProperty("ref")
    public URI toUri() {
        return URI.create(basePath + "/" + resourceId.toString());
    }
}
