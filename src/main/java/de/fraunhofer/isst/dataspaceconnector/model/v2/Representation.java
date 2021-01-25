package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

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
public class Representation extends BaseResource {
    private String title;
    private String mediaType;
    private String language;

    @MapKey(name = "id")
    @OneToMany
    private Map<UUID, Artifact> artifacts;
}
