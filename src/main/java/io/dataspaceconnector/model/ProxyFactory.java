package io.dataspaceconnector.model;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class ProxyFactory implements AbstractFactory<Proxy, ProxyDesc> {

    @Override
    public Proxy create(final ProxyDesc desc) {
        final var proxy = new Proxy();
        proxy.setNoProxyURI(new ArrayList<>());

        update(proxy, desc);

        return proxy;
    }

    @Override
    public boolean update(final Proxy entity, final ProxyDesc desc) {
        return false;
    }
}
