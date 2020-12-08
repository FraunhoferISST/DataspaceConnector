package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.Date;
import java.util.UUID;

/**
 * ConnectorResource interface.
 */
public interface ConnectorResource {

    UUID getUuid();

    void setUuid(UUID uuid);

    Date getCreated();

    void setCreated(Date created);

    Date getModified();

    void setModified(Date modified);

    ResourceMetadata getResourceMetadata();

    void setResourceMetadata(ResourceMetadata resourceMetadata);

    String getData();

    void setData(String data);
}
