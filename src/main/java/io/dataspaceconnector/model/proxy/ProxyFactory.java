package io.dataspaceconnector.model.proxy;

import java.util.ArrayList;

import io.dataspaceconnector.model.base.AbstractFactory;
import org.springframework.stereotype.Component;

@Component
public class ProxyFactory extends AbstractFactory<Proxy, ProxyDesc> {

    @Override
    protected Proxy initializeEntity(final ProxyDesc desc) {
        final var proxy = new Proxy();
        proxy.setExclusions(new ArrayList<>());

        return proxy;
    }
}
