package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * This class provides a model to handle agreed resource contracts.
 */
@Data
@Entity
@Table
public class ResourceContract {
    @Id
    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("contract")
    @Column(columnDefinition = "TEXT")
    private String contract;

    /**
     * Constructor for ResourceContract.
     */
    public ResourceContract() {
    }

    /**
     * Constructor for ResourceContract.
     *
     * @param contract a {@link String} object.
     */
    public ResourceContract(UUID uuid, String contract) {
        this.uuid = uuid;
        this.contract = contract;
    }

    public UUID getId() {
        return uuid;
    }

    public void setId(UUID uuid) {
        this.uuid = uuid;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }
}
