package io.dataspaceconnector.model;

import java.net.URI;

import lombok.Data;

@Data
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
