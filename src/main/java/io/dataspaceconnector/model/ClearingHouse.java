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

/**
 * The Clearing House is an intermediary that provides clearing and settlement services for all data exchange
 * transactions.
 */
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

    /**
     * The access url of the clearing house.
     */
    private URI accessUrl;

    /**
     * The title of the clearing house.
     */
    private String title;

    /**
     * The status of registration.
     */
    @Enumerated(EnumType.STRING)
    private RegisterStatus registerStatus;

    /**
     * The date specification.
     */
    private ZonedDateTime lastSeen;

}
