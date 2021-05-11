package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProxyDesc extends AbstractDescription<Proxy>{

    private URI proxyURI;

    private List<URI> noProxyURI;

    private ProxyAuthentication proxyAuthentication;
}
