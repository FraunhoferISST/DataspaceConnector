package io.dataspaceconnector.model;

import java.util.ArrayList;

import io.dataspaceconnector.model.base.Factory;
import org.springframework.stereotype.Component;

@Component
public class ProxyFactory implements Factory<Proxy, ProxyDesc> {

    @Override
    public Proxy create(final ProxyDesc desc) {
        final var proxy = new Proxy();
        proxy.setExclusions(new ArrayList<>());

        update(proxy, desc);

        return proxy;
    }

    @Override
    public boolean update(final Proxy entity, final ProxyDesc desc) {
        return false;
    }
}
