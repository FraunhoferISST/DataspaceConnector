package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * The title of the contract.
     */
    private String title;

    /**
     * Contract start time and date.
     */
    private ZonedDateTime start;

    /**
     * Contract end time and date.
     */
    private ZonedDateTime end;
}
