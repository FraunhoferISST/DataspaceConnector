package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class ProxyFactory implements AbstractFactory<Proxy, ProxyDesc> {

    @Override
    public Proxy create(ProxyDesc desc) {
        return null;
    }

    @Override
    public boolean update(Proxy entity, ProxyDesc desc) {
        return false;
    }
}
