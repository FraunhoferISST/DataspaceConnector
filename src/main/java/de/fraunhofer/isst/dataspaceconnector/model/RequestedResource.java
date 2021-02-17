package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URI;
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

    @JsonProperty("ownerURI")
    private URI ownerURI;

    @JsonProperty("originalUUID")
    private UUID originalUUID;

    @JsonProperty("contractAgreement")
    private URI contractAgreement;

    @JsonProperty("requestedArtifact")
    private URI requestedArtifact;

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
     * Get owner URI
     */
    public URI getOwnerURI() {
        return ownerURI;
    }

    /**
     * Set owner URI
     */
    public void setOwnerURI(URI ownerURI) {
        this.ownerURI = ownerURI;
    }

    /**
     * Get original UUID
     */
    public UUID getOriginalUUID() {
        return originalUUID;
    }

    /**
     * Set original UUID
     */
    public void setOriginalUUID(UUID originalUUID) {
        this.originalUUID = originalUUID;
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
     * Get Contract Agreement URI
     */
    public URI getContractAgreement() {
        return contractAgreement;
    }

    /**
     * Set Contract Agreement URI
     */
    public void setContractAgreement(URI contractAgreement) {
        this.contractAgreement = contractAgreement;
    }

    /**
     * Return URI of requested artifact.
     */
    public URI getRequestedArtifact() {
        return requestedArtifact;
    }

    /**
     * Set URI of requested artifact.
     */
    public void setRequestedArtifact(URI requestedArtifact) {
        this.requestedArtifact = requestedArtifact;
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
        this.setModified(new Date());
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
