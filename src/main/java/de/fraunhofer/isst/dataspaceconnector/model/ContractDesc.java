package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Describes a contract's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractDesc extends AbstractDescription<Contract> {
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
