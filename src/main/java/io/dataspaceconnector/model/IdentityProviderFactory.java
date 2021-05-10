package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class IdentityProviderFactory implements AbstractFactory<IdentityProvider, IdentityProviderDesc> {

    @Override
    public IdentityProvider create(IdentityProviderDesc desc) {
        return null;
    }

    @Override
    public boolean update(IdentityProvider entity, IdentityProviderDesc desc) {
        return false;
    }
}
