package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

/**
 * The interface for describing data in the backend.
 */
@lombok.Data
@Entity
@Inheritance
@Setter(AccessLevel.NONE)
public class Data {

    /**
     * The primary key of the data.
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    @ToString.Exclude
    private Long id;
}
