package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class TruststoreFactory
        implements AbstractFactory<Truststore, TruststoreDesc> {
    @Override
    public Truststore create(final TruststoreDesc desc) {
        final var entity = new Truststore();

        update(entity, desc);

        return entity;
    }

    @Override
    public boolean update(final Truststore entity, final TruststoreDesc desc) {
        return false;
    }
}
