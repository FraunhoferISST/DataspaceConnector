package io.dataspaceconnector.model;

import java.net.URI;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
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
