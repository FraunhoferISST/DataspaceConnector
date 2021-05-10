package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.net.URI;

@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AppStoreCatalog extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    private URI uri;

    private String title;

    @Enumerated(EnumType.STRING)
    private RegisterStatus registerStatus;

    // ToDO: list of apps must be completed at this point

    private String lastSeen;

}
