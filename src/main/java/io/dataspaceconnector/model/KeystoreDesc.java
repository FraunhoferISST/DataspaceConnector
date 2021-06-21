package io.dataspaceconnector.model;

import java.net.URI;

import lombok.Data;

@Data
public class KeystoreDesc extends AbstractDescription<Keystore> {
    /**
     * The key store.
     */
    private URI location;

    /**
     * The key store password.
     */
    private String password;
}
