package io.dataspaceconnector.model.endpoint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;

/**
 * Contains the data kept in an artifact.
 */
@Entity
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AppEndpointImpl extends AppEndpoint {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The exposed port.
     */
    private int exposedPort;

}
