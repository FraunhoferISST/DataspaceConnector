package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.net.URI;
import java.util.List;

/**
 * Describing broker's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrokerDesc extends AbstractDescription<Broker> {

    /**
     * The access url of the broker.
     */
    private URI accessUrl;

    /**
     * The title of the broker.
     */
    private String title;

    /**
     * The status of registration.
     */
    private RegisterStatus status;

    /**
     * The list of resources.
     */
    private List<OfferedResource> offeredResources;
}
