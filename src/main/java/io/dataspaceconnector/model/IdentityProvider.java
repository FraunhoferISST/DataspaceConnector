package io.dataspaceconnector.model;

import io.dataspaceconnector.model.utils.UriConverter;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URI;
import java.time.ZonedDateTime;

/**
 * Entity class for the identity provider.
 */
@Entity
@Inheritance
@Table(name = "identityprovider")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE identityprovider SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@RequiredArgsConstructor
public class IdentityProvider extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the identity provider.
     */
    @Convert(converter = UriConverter.class)
    private URI accessUrl;

    /**
     * The title of the identity provider.
     */
    private String title;

    /**
     * The registration status.
     */
    @Enumerated(EnumType.STRING)
    private RegisterStatus registerStatus;

    /**
     * The date specification.
     */
    private ZonedDateTime lastSeen;
}
