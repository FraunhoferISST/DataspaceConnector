package de.fraunhofer.isst.dataspaceconnector.view;

import java.util.UUID;

import org.springframework.hateoas.Link;

public interface SelfLinking {
    Link getSelfLink(UUID entityId);
}
