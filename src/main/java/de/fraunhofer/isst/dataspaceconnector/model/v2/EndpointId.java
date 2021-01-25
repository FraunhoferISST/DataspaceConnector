package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

@Data
@Embeddable
public class EndpointId implements Serializable {
    private String basePath;

    private UUID resourceId;

    public EndpointId() {
    }

    public EndpointId(final String basePath, final UUID resourceId) {
        this.basePath = basePath;
        this.resourceId = resourceId;
    }

    public URI toUri() {
        return URI.create(basePath + "/" + resourceId.toString());
    }
}
