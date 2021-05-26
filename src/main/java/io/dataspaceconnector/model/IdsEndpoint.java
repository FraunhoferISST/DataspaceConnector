package io.dataspaceconnector.model;

import io.dataspaceconnector.model.utils.UriConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.net.URI;

/**
 * Entity for managing ids endpoints.
 */
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class IdsEndpoint extends Endpoint{

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The absolute path of the generic endpoint.
     */
    @Convert(converter = UriConverter.class)
    private URI accessURL;

    /**
     * Default constructor.
     */
    protected IdsEndpoint() {
        super();
    }

}
