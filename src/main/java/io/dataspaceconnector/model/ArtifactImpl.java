package io.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Contains the data kept in an artifact.
 */
@Entity
@Table(name = "artifact")
@SQLDelete(sql = "UPDATE artifact SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
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
