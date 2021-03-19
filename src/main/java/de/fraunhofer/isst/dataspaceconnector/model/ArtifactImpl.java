package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * Contains the data kept in an artifact.
 */
@lombok.Data
@Entity
@Table
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
public class ArtifactImpl extends Artifact {
    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The data stored in the artifact.
     **/
    @OneToOne(cascade = { CascadeType.ALL })
    @JsonInclude
    @ToString.Exclude
    private Data data;
}
