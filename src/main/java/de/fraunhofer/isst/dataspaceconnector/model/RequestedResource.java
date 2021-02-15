package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public final class RequestedResource extends Resource {

    /**
     * Default constructor.
     */
    protected RequestedResource() {
        super();
    }
}
