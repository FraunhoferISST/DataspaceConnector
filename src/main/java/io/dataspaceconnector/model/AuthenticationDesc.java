package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Describing the authentication's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthenticationDesc extends AbstractDescription<Authentication> {

    /**
     * The username for the authentication.
     */
    private String username;

    /**
     * The password for the authentication.
     */
    private String password;
}
