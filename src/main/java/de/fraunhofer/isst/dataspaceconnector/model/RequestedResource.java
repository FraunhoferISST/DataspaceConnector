package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * This class provides a custom data resource with an id, data and metadata to be saved in a h2
 * database.
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
     */
    public RequestedResource(Date created, Date modified, ResourceMetadata resourceMetadata,
        String data, Integer accessed) {
        this.created = created;
        this.modified = modified;
        this.resourceMetadata = resourceMetadata;
        this.data = data;
        this.accessed = accessed;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public ResourceMetadata getResourceMetadata() {
        return resourceMetadata;
    }

    @Override
    public void setResourceMetadata(ResourceMetadata resourceMetadata) {
        this.resourceMetadata = resourceMetadata;
    }

    @Override
    public String getData() {
        incrementDataAccess();
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }

    public Integer getAccessed() {
        return accessed;
    }

    private void incrementDataAccess() {
        this.accessed++;
    }
}
