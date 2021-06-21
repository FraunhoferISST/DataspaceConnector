package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class KeystoreFactory implements AbstractFactory<Keystore, KeystoreDesc> {

    @Override
    public Keystore create(final KeystoreDesc desc) {
        final var entity = new Keystore();

        update(entity, desc);

        return entity;
    }

    @Override
    public boolean update(final Keystore entity, final KeystoreDesc desc) {
        return false;
    }
}
