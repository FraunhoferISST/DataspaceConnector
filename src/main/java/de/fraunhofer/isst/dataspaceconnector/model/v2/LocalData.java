package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@lombok.Data
@Entity
@Table
@Setter(AccessLevel.PACKAGE)
public class LocalData extends Data {
    private String value;
}
