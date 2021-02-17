package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@MappedSuperclass
@Setter(AccessLevel.NONE)
public class BaseResource implements Serializable {
    @Id
    @GeneratedValue
    private UUID id;

    @CreationTimestamp
    private Date creationDate;

    @UpdateTimestamp
    private Date lastModificationDate;
}
