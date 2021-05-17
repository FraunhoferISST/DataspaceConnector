package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Describing identity provider's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IdentityProviderDesc extends AbstractDescription<IdentityProvider> {

    /**
     * The access url of the identity provider.
     */
    private URI accessUrl;

    /**
     * The title of the identity provider.
     */
    private String title;

    /**
     * The registration status.
     */
    private RegisterStatus registerStatus;
}
