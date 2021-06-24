package io.dataspaceconnector.model.keystore;

import java.net.URI;

import io.dataspaceconnector.model.base.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KeystoreDesc extends Description {
    /**
     * The key store.
     */
    private URI location;

    /**
     * The key store password.
     */
    private String password;
}
