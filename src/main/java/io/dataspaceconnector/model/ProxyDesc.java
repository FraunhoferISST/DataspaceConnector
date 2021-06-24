package io.dataspaceconnector.model;

import java.net.URI;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProxyDesc extends AbstractDescription<Proxy> {
    private URI       name;
    private List<URI> exclusions;
    private AuthenticationDesc authentication;
}
