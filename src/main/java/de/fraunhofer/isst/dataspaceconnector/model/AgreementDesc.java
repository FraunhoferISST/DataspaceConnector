package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.List;

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
     * Indicates whether both parties have agreed.
     */
    private boolean confirmed;

    /**
     * The definition of the contract.
     **/
    private String value;

    /**
     * The artifacts this agreement refers to.
     */
    private List<Artifact> artifacts;
}
