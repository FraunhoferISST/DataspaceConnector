package io.dataspaceconnector.model;

import javax.persistence.MappedSuperclass;

import io.dataspaceconnector.model.base.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class NamedEntity extends Entity {
    private String title;
    private String description;
}
