package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table
@Data
public class Endpoint {
    @EmbeddedId
    private EndpointId id;

    private UUID internalId;

    @OneToOne
    private Endpoint newLocation;
}
