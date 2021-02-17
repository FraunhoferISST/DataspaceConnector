package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public final class OfferedResource extends Resource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    protected OfferedResource() {
        super();
    }
}
