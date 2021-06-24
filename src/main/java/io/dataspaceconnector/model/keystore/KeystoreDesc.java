package io.dataspaceconnector.model.keystore;

import java.net.URI;

import io.dataspaceconnector.model.base.AbstractDescription;
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
