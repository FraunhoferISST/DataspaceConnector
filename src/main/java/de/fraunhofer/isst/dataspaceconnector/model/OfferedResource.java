package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * This class describes an offered resource.
 */
@Data
@Entity
@Table
public class OfferedResource implements ConnectorResource {

    @Id
    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("modified")
    private Date modified;

    @NotNull
    @Column(columnDefinition = "BYTEA")
    @JsonProperty("metadata")
    private ResourceMetadata resourceMetadata;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("data")
    private String data;

    /**
     * Constructor for OfferedResource.
     */
    public OfferedResource() {

    }

    /**
     * Constructor with parameters for OfferedResource.
     *
     * @param created The resource creation date
     * @param modified The date when the resource was last modified
     * @param resourceMetadata The metadata associated with this resource
     * @param data The data associated with this resource
     */
    public OfferedResource(UUID uuid, Date created, Date modified,
        ResourceMetadata resourceMetadata, String data) {
        this.uuid = uuid;
        this.created = created;
        this.modified = modified;
        this.resourceMetadata = resourceMetadata;
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getUuid() {
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getCreated() {
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreated(Date created) {
        this.created = created;
        this.modified = new Date();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getModified() {
        return modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceMetadata getResourceMetadata() {
        return resourceMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResourceMetadata(ResourceMetadata resourceMetadata) {
        this.setModified(new Date());
        this.resourceMetadata = resourceMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getData() {
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setData(String data) {
        this.setModified(new Date());
        this.data = data;
    }
}
