package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Base type for all entities.
 */
@Data
@MappedSuperclass
@Setter(AccessLevel.NONE)
public class BaseEntity implements Serializable {
    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The primary key of all entities.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * The date when this entity was persisted the first time.
     */
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    @CreationTimestamp
    private Date creationDate;

    /**
     * The date of the last persistent modification.
     */
    @Column(name = "modified_date", nullable = false)
    @LastModifiedDate
    @UpdateTimestamp
    private Date modificationDate;
}
