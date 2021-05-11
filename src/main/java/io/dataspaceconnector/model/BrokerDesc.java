package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BrokerDesc extends AbstractDescription<Broker>{

    private URI accessUrl;

    private String title;

    private RegisterStatus status;

    private List<OfferedResource> offeredResources;
}
