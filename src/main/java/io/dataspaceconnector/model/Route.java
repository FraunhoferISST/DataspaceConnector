package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

/**
 *
 */
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

    /**
     * The deploy method of the route.
     */
    @Enumerated(EnumType.STRING)
    private DeployMethod deployMethod;

    /**
     * List of subroutes.
     */
    @OneToMany
    private List<Route> subRoutes;

    /**
     * The route configuration.
     */
    private String routeConfiguration;

    /**
     * The start endpoint of the route.
     */
    @OneToOne
    private Endpoint startEndpoint;

    /**
     * The last endpoint of the route.
     */
    @OneToOne
    private Endpoint endEndpoint;

    /**
     * List of offered resources.
     */
    @OneToMany
    private List<OfferedResource> offeredResources;

}
