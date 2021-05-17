package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.IdentityProvider;
import io.dataspaceconnector.model.IdentityProviderDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for identity provider.
 */
@Service
@NoArgsConstructor
public class IdentityProviderService extends BaseEntityService<IdentityProvider, IdentityProviderDesc> {
}
