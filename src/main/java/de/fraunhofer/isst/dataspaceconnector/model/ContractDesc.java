package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.Date;

/**
 * Describes a contract's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractDesc extends AbstractDescription<Contract> {

    /**
     * The contract id on provider side.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteId;

    /**
     * The consumer signing the contract.
     */
    private URI consumer;

    /**
     * The provider signing the contract.
     */
    private URI provider;

    /**
     * The date of the contract conclusion.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date date;

    /**
     * The title of the contract.
     */
    private String title;

    /**
     * Contract start time and date.
     */
    private Date start;

    /**
     * Contract end time and date.
     */
    private Date end;
}
