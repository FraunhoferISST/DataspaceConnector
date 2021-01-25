package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table
@Setter(AccessLevel.PACKAGE)
public class Rule extends BaseResource {
    private String title;
    private String value;
}
