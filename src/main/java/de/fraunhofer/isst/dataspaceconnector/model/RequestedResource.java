package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public final class RequestedResource extends Resource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

//    @JsonProperty("ownerURI")
//    private URI ownerURI;
//
//    @JsonProperty("originalUUID")
//    private UUID originalUUID;
//
//    @JsonProperty("contractAgreement")
//    private URI contractAgreement;
//
//    @JsonProperty("requestedArtifact")
//    private URI requestedArtifact;

    /**
     * Default constructor.
     */
    protected RequestedResource() {
        super();
    }
}
