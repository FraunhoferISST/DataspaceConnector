package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

/**
 * Describes a contract agreement's properties.
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
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
