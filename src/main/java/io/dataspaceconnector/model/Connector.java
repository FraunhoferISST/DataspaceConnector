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
 * Entity, which holds connector information.
 */
@Entity
@Table(name = "connector")
@SQLDelete(sql = "UPDATE connector SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Connector extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the connector.
     */
    private URI accessUrl;

    /**
     * The title of the connector.
     */
    private String title;

    /**
     * The registration status of the connector.
     */
    @Enumerated(EnumType.STRING)
    private RegisterStatus registerStatus;

    /**
     * The date specification.
     */
    private ZonedDateTime lastSeen;
}
