package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * This class provides a custom data resource with an id, data and metadata to be saved in a h2
 * database.
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
     */
    public OfferedResource(UUID uuid, Date created, Date modified,
        ResourceMetadata resourceMetadata, String data) {
        this.uuid = uuid;
        this.created = created;
        this.modified = modified;
        this.resourceMetadata = resourceMetadata;
        this.data = data;
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
        this.modified = new Date();
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
        this.setModified(new Date());
        this.resourceMetadata = resourceMetadata;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.setModified(new Date());
        this.data = data;
    }
}
