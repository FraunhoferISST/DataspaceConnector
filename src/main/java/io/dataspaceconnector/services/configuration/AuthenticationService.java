package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Authentication;
import io.dataspaceconnector.model.AuthenticationDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for authentications.
 */
@Service
@NoArgsConstructor
public class AuthenticationService extends BaseEntityService<Authentication, AuthenticationDesc> {
}
