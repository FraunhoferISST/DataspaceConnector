package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import javax.persistence.ElementCollection;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A resource describes offered or requested data.
 */
@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Resource extends BaseResource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The title of the resource.
     */
    private String title;

    /**
     * The description of the resource.
     */
    private String description;

    /**
     * The keywords of the resource.
     */
    @ElementCollection
    private List<String> keywords;

    /**
     * The publisher of the resource.
     */
    private URI publisher;

    /**
     * The language of the resource.
     */
    private String language;

    /**
     * The licence of the resource.
     */
    private URI licence;

    /**
     * The version of the resource.
     */
    @Version
    private long version;

    /**
     * The representation available for the resource.
     */
    @MapKey(name = "id")
    @OneToMany
    private Map<UUID, Representation> representations;

    /**
     * The contracts available for the resource.
     */
    @MapKey(name = "id")
    @OneToMany
    private Map<UUID, Contract> contracts;
}
