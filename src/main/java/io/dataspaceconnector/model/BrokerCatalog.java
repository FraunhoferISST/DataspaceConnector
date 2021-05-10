package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;
import java.util.List;

@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class BrokerCatalog extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    private URI uri;

    private String title;

    @Enumerated(EnumType.STRING)
    private RegisterStatus status;

    @OneToMany
    private List<OfferedResource> offeredResources;

    private String credentials;

    private String lastSeen;


}
