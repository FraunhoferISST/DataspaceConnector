package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * This class describes a resource requested.
 */
@Data
@Entity
@Table
public class RequestedResource implements ConnectorResource {

    @Id
    @GeneratedValue
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

    @JsonProperty("accessed")
    private Integer accessed;

    /**
     * Constructor for RequestedResource.
     */
    public RequestedResource() {

    }

    /**
     * Constructor with parameters for RequestedResource.
     *
     * @param created The resource creation date
     * @param modified The date when the resource was last modified
     * @param resourceMetadata The metadata associated with this resource
     * @param data The data associated with this resource
     * @param accessed The number of times the data was accessed
     */
    public RequestedResource(Date created, Date modified, ResourceMetadata resourceMetadata,
        String data, Integer accessed) {
        this.created = created;
        this.modified = modified;
        this.resourceMetadata = resourceMetadata;
        this.data = data;
        this.accessed = accessed;
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
        this.resourceMetadata = resourceMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getData() {
        incrementDataAccess();
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Describes how often the data has been accessed
     *
     * @return The number of times the data has been accessed
     */
    public Integer getAccessed() {
        return accessed;
    }

    private void incrementDataAccess() {
        this.accessed++;
    }
}
