package io.dataspaceconnector.model;

import io.dataspaceconnector.model.utils.UriConverter;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import java.net.URI;

/**
 * Entity which manages the endpoints.
 */
@Entity
@Inheritance
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "endpoint")
@RequiredArgsConstructor
public class Endpoint extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The documentation for the endpoint.
     */
    @Convert(converter = UriConverter.class)
    private URI endpointDocumentation;

    /**
     * The information for the endpoint.
     */
    private String endpointInformation;

    /**
     * The inbound path.
     */
    private String inboundPath;

    /**
     * The outbound path.
     */
    private String outboundPath;

    /**
     * The type of the endpoint.
     */
    private EndpointType endpointType;
}
