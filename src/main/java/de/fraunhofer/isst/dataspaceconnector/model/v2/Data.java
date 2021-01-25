package de.fraunhofer.isst.dataspaceconnector.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

@lombok.Data
@Entity
@Inheritance
@Setter(AccessLevel.NONE)
public class Data {
    @Id
    @GeneratedValue
    @JsonIgnore
    @ToString.Exclude
    private Long id;
}
