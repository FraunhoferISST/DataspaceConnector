package io.dataspaceconnector.model;

import io.dataspaceconnector.model.base.Factory;
import org.springframework.stereotype.Component;

@Component
public class KeystoreFactory implements Factory<Keystore, KeystoreDesc> {

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
