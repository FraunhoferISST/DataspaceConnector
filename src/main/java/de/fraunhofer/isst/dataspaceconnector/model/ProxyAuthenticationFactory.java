package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class ProxyAuthenticationFactory implements AbstractFactory<ProxyAuthentication, ProxyAuthenticationDesc> {


    @Override
    public ProxyAuthentication create(ProxyAuthenticationDesc desc) {
        return null;
    }

    @Override
    public boolean update(ProxyAuthentication entity, ProxyAuthenticationDesc desc) {
        return false;
    }
}
