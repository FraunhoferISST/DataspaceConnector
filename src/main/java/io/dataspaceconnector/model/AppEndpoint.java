package io.dataspaceconnector.model;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.net.URI;

@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Entity
@EqualsAndHashCode(callSuper = true)
public class AppEndpoint extends Endpoint {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    private URI accessURL;

    private String mediaType;

    private int appEndpointPort;

    private String appEndpointProtocol;

    private String language;

    @Enumerated(EnumType.STRING)
    private AppEndpointType appEndpointType;
}
