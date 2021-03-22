package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

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

    /**
     * The catalogs in which this resource is used.
     */
    @ManyToMany(mappedBy = "requestedResources")
    private List<Catalog> catalogs;
}
