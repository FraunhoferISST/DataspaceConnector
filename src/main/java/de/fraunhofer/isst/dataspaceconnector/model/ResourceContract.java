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
    private UUID id;

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
     * @param id The id
     * @param contract The contract's text.
     */
    public ResourceContract(UUID id, String contract) {
        this.id = id;
        this.contract = contract;
    }
}
