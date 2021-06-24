package io.dataspaceconnector.model.proxy;

import java.net.URI;
import java.util.List;

import io.dataspaceconnector.model.AuthenticationDesc;
import io.dataspaceconnector.model.base.AbstractDescription;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProxyDesc extends AbstractDescription<Proxy> {
    private URI       name;
    private List<URI>          exclusions;
    private AuthenticationDesc authentication;
}
