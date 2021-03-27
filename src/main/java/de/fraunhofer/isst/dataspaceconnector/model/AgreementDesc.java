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
     * Indicates whether both parties have agreed.
     */
    private boolean confirmed;

    /**
     * The definition of the contract.
     **/
    private String value;
}
