package io.dataspaceconnector.model;

import java.net.URI;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TruststoreDesc extends AbstractDescription<Truststore> {
    /**
     * The trust store.
     */
    private URI name;

    /**
     * The password of the trust store.
     */
    private String password;
}
