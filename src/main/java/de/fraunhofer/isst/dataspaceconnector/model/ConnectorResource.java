package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.Date;
import java.util.UUID;

/**
 * ConnectorResource interface.
 */
public interface ConnectorResource {

    /**
     * Get the resource id
     *
     * @return The id
     */
    UUID getUuid();

    /**
     * Set the resource id
     *
     * @param uuid The new id
     */
    void setUuid(UUID uuid);

    /**
     * Get the creation date
     *
     * @return The creation date
     */
    Date getCreated();

    /**
     * Set the creation date
     *
     * @param created The creation date
     */
    void setCreated(Date created);

    /**
     * Get the date of the last modification
     *
     * @return The modification date
     */
    Date getModified();

    /**
     * Set the date of the last modification
     *
     * @param modified The modification date
     */
    void setModified(Date modified);

    /**
     * Get the metadata associated with this resource
     *
     * @return The metadata
     */
    ResourceMetadata getResourceMetadata();

    /**
     * Set the metadata associated with this resource
     *
     * @param resourceMetadata The metadata
     */
    void setResourceMetadata(ResourceMetadata resourceMetadata);

    /**
     * Get the data associated with this resource
     *
     * @return The data
     */
    String getData();

    /**
     * Set the metadata associated with this resource
     *
     * @param data The data
     */
    void setData(String data);
}
