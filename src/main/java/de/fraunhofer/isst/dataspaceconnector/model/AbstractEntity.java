package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    @Setter(AccessLevel.PUBLIC)
    @Column(name = "id", unique = true, nullable = false)
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

    @JsonAnySetter
    public void set(final String key, final String value) {
        if(additional == null)
            additional = new HashMap<String, String>();

        additional.put(key, value);
    }

    @ElementCollection
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    @JsonUnwrapped
    private Map<String, String> additional;
}
