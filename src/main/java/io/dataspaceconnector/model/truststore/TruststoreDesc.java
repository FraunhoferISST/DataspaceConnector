package io.dataspaceconnector.model.truststore;

import java.net.URI;

import io.dataspaceconnector.model.base.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TruststoreDesc extends Description {
    /**
     * The trust store.
     */
    private URI name;

    /**
     * The password of the trust store.
     */
    private String password;
}
