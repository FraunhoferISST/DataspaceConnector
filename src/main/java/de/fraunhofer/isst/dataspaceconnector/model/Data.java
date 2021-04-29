package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The interface for describing data in the backend.
 */
@Entity
@Inheritance
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@RequiredArgsConstructor
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
