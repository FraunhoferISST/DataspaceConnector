package io.dataspaceconnector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Entity for managing generic endpoints.
 */
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GenericEndpoint extends Endpoint {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The absolute path of the generic endpoint.
     */
    private String absolutePath;
}
