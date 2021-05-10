package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "route")
@SQLDelete(sql = "UPDATE route SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Route extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private DeployMethod deployMethod;

    @OneToMany
    private List<Route> subRoutes;

    private String routeConfiguration;

    @OneToOne
    private Endpoint startEndpoint;

    @OneToOne
    private Endpoint endpoint;

    @OneToMany
    private List<OfferedResource> offeredResources;

}
