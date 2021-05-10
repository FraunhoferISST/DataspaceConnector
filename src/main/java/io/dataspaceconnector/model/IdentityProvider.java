package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URI;
import java.time.ZonedDateTime;

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

    private URI accessUrl;

    private String title;

    @Enumerated(EnumType.STRING)
    private RegisterStatus registerStatus;

    private ZonedDateTime lastSeen;
}
