package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.List;

/**
 * Describing proxy's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProxyDesc extends AbstractDescription<Proxy> {

    /**
     * The proxy uri.
     */
    private URI proxyURI;

    /**
     * List of no proxy uris.
     */
    private List<URI> noProxyURI;

    /**
     * The authentication for the proxy.
     */
    private Authentication authentication;
}
