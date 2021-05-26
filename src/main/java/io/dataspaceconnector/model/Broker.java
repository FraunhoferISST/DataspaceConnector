package io.dataspaceconnector.model;

import io.dataspaceconnector.model.utils.UriConverter;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * The entity where connectors and resources can be registered.
 */
@Entity
@Table(name = "broker")
@SQLDelete(sql = "UPDATE broker SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Broker extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the broker.
     */
    @Convert(converter = UriConverter.class)
    private URI accessUrl;

    /**
     * The title of the broker.
     */
    private String title;

    /**
     * The status of registration.
     */
    @Enumerated(EnumType.STRING)
    private RegisterStatus status;

    /**
     * The list of resources.
     */
    @OneToMany
    private List<OfferedResource> offeredResources;

    /**
     * Necessary credentials.
     */
    private String credentials;

    /**
     * The date specification.
     */
    private ZonedDateTime lastSeen;

}
