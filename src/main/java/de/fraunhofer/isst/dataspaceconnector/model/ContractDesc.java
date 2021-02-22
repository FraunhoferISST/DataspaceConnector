package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
