package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.Date;
import java.util.UUID;

/**
 * <p>ConnectorResource interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface ConnectorResource {
    /**
     * <p>getUuid.</p>
     *
     * @return a {@link java.util.UUID} object.
     */
    UUID getUuid();

    /**
     * <p>setUuid.</p>
     *
     * @param uuid a {@link java.util.UUID} object.
     */
    void setUuid(UUID uuid);

    /**
     * <p>getCreated.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    Date getCreated();

    /**
     * <p>setCreated.</p>
     *
     * @param created a {@link java.util.Date} object.
     */
    void setCreated(Date created);

    /**
     * <p>getModified.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    Date getModified();

    /**
     * <p>setModified.</p>
     *
     * @param modified a {@link java.util.Date} object.
     */
    void setModified(Date modified);

    /**
     * <p>getResourceMetadata.</p>
     *
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     */
    ResourceMetadata getResourceMetadata();

    /**
     * <p>setResourceMetadata.</p>
     *
     * @param resourceMetadata a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     */
    void setResourceMetadata(ResourceMetadata resourceMetadata);

    /**
     * <p>getData.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getData();

    /**
     * <p>setData.</p>
     *
     * @param data a {@link java.lang.String} object.
     */
    void setData(String data);
}
