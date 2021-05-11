package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdentityProviderDesc extends AbstractDescription<IdentityProvider>{

    private URI accessUrl;

    private String title;

    private RegisterStatus registerStatus;
}
