package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Contract extends BaseResource {
    private String title;

    @MapKey(name = "id")
    @OneToMany
    private Map<UUID, Rule> rules;
}
