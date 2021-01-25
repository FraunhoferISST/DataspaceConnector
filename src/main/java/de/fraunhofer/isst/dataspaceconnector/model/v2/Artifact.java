package de.fraunhofer.isst.dataspaceconnector.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@lombok.Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Artifact extends BaseResource {
    private String title;
    //Long byteSize;
    //Long checksum;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JsonInclude
    @ToString.Exclude
    private Data data;
}
