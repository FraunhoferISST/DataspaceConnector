package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Describes a contract agreement's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AgreementDesc extends AbstractDescription<Agreement> {
    /**
     * The agreement id on provider side.
     */
    private URI remoteId;

    /**
     * The definition of the contract.
     **/
    private String value;
}
