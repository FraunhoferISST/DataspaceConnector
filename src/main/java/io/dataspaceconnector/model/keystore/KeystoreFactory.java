package io.dataspaceconnector.model.keystore;

import io.dataspaceconnector.model.base.AbstractFactory;
import org.springframework.stereotype.Component;

@Component
public class KeystoreFactory extends AbstractFactory<Keystore, KeystoreDesc> {
    @Override
    protected Keystore initializeEntity(final KeystoreDesc desc) {
        return new Keystore();
    }
}
