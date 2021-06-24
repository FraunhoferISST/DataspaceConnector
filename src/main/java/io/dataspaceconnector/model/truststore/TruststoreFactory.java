package io.dataspaceconnector.model.truststore;

import io.dataspaceconnector.model.base.AbstractFactory;
import org.springframework.stereotype.Component;

@Component
public class TruststoreFactory extends AbstractFactory<Truststore, TruststoreDesc> {
    @Override
    protected Truststore initializeEntity(final TruststoreDesc desc) {
        return new Truststore();
    }
}
