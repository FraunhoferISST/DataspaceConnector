package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

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

    private URI accessUrl;

    private String title;

    @Enumerated(EnumType.STRING)
    private RegisterStatus status;

    @OneToMany
    private List<OfferedResource> offeredResources;

    private String credentials;

    private ZonedDateTime lastSeen;

}
