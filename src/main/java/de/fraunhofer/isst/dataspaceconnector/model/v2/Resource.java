package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table
@Setter(AccessLevel.PACKAGE)
public class Resource extends BaseResource {
    private String title;
    private String description;
    private String keywords;
    private String publisher;
    private String language;
    private String licence;

    @Version
    private long version;

    @MapKey(name = "id")
    @OneToMany
    private Map<UUID, Representation> representations;

    @MapKey(name = "id")
    @OneToMany
    private Map<UUID, Contract> contracts;
}
