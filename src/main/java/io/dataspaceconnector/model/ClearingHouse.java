package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.net.URI;
import java.time.ZonedDateTime;

@Entity
@Table(name = "clearinghouse")
@SQLDelete(sql = "UPDATE clearinghouse SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ClearingHouse extends AbstractEntity {

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
