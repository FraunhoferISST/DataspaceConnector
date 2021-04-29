package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Base type for all entities.
 */
@Data
@MappedSuperclass
@Setter(AccessLevel.NONE)
public class AbstractEntity implements Serializable {
    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The primary key of all entities.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "id", unique = true, nullable = false)
    @EqualsAndHashCode.Exclude
    private UUID id;

    /**
     * The date when this entity was persisted the first time.
     */
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    @CreationTimestamp
    private ZonedDateTime creationDate;

    /**
     * The date of the last persistent modification.
     */
    @Column(name = "modified_date", nullable = false)
    @LastModifiedDate
    @UpdateTimestamp
    private ZonedDateTime modificationDate;

    /**
     * Contains all additional fields that may have been defined but
     * could not be mapped.
     */
    @ElementCollection
    @Setter(AccessLevel.PACKAGE)
    private Map<String, String> additional;
}
