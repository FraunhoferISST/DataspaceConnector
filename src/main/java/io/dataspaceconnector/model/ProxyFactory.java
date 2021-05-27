/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

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
     * The default uris assigned to all no proxy list..
     */
    public static final List<URI> DEFAULT_URI_LIST = List.of(URI.create("https://noproxylist"));

    /**
     * @param desc The description of the entity.
     * @return The new proxy entity.
     */
    @Override
    public Proxy create(final ProxyDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var proxy = new Proxy();
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
        final var hasUpdatedNoProxyList = updateNoProxyList(proxy, desc.getNoProxyURI());

        return hasUpdatedProxyUri || hasUpdatedNoProxyList;
    }

    /**
     * @param proxy       The proxy.
     * @param noProxyUris The no proxy list.
     * @return True, if list is updated.
     */
    private boolean updateNoProxyList(final Proxy proxy, final List<URI> noProxyUris) {
        final var newProxyList =
                MetadataUtils.updateUriList(proxy.getNoProxyURI(), noProxyUris, DEFAULT_URI_LIST);
        newProxyList.ifPresent(proxy::setNoProxyURI);

        return newProxyList.isPresent();
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
