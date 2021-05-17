package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;

/**
 * Creates and updates proxies.
 */
@Component
public class ProxyFactory implements AbstractFactory<Proxy, ProxyDesc> {

    /**
     * The default uri.
     */
    private static final URI DEFAULT_URI = URI.create("https://access");

    /**
     * @param desc The description of the entity.
     * @return The new proxy entity.
     */
    @Override
    public Proxy create(final ProxyDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var proxy = new Proxy();
        proxy.setNoProxyURI(new ArrayList<>());
        proxy.setAuthentication(null);

        update(proxy, desc);

        return proxy;
    }

    /**
     * @param proxy The entity to be updated.
     * @param desc  The description of the new entity.
     * @return True, if proxy is updated.
     */
    @Override
    public boolean update(final Proxy proxy, final ProxyDesc desc) {
        Utils.requireNonNull(proxy, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedProxyUri = updateProxyUri(proxy, desc.getProxyURI());


        return false;
    }

    /**
     * @param proxy    The proxy.
     * @param proxyUri The proxy uri.
     * @return True, if proxy uri is updated.
     */
    private boolean updateProxyUri(final Proxy proxy, final URI proxyUri) {

        final var newProxyUri = MetadataUtils.updateUri(proxy.getProxyURI(), proxyUri,
                DEFAULT_URI);
        newProxyUri.ifPresent(proxy::setProxyURI);

        return newProxyUri.isPresent();
    }
}
