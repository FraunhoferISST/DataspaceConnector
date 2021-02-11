package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

/**
 * An artifact stores and encapsulates data.
 */
@Getter
@RequiredArgsConstructor
@Inheritance
@Entity
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public abstract class Artifact extends BaseResource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The title of the catalog.
     **/
    private String title;

    private Long numAccessed;

    public void incrementAccessCounter() {
        numAccessed += 1;
    }
}
