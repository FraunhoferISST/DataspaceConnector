package io.dataspaceconnector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.net.URI;

/**
 * The app endpoint can be used and connected to other endpoints to perform operations on the data.
 */
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class AppEndpoint extends Endpoint {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the endpoint.
     */
    private URI accessURL;

    /**
     * The file name extension of the data.
     */
    private String mediaType;

    /**
     * The port number of the app endpoint.
     */
    private int appEndpointPort;

    /**
     * The protocol of the app endpoint.
     */
    private String appEndpointProtocol;

    /**
     * The used language.
     */
    private String language;

    /**
     * The type of the app endpoint.
     */
    @Enumerated(EnumType.STRING)
    private AppEndpointType appEndpointType;

    /**
     * Default constructor.
     */
    protected AppEndpoint() {
        super();
    }
}
