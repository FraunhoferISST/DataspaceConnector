package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Describes a contract agreement's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AgreementDesc extends AbstractDescription<Agreement> {
    /**
     * The definition of the contract.
     **/
    private String value;
}
