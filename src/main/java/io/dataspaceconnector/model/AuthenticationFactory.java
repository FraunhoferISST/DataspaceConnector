package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates authentications.
 */
@Component
public class AuthenticationFactory implements AbstractFactory<Authentication, AuthenticationDesc> {

    /**
     * @param desc The description of the entity.
     * @return New authentication entity.
     */
    @Override
    public Authentication create(final AuthenticationDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var auth = new Authentication();
        update(auth, desc);

        return auth;
    }

    /**
     * @param auth The entity to be updated.
     * @param desc The description of the new entity.
     * @return True, if authentication is updated.
     */
    @Override
    public boolean update(final Authentication auth, final AuthenticationDesc desc) {
        Utils.requireNonNull(auth, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedUsername = updateUsername(auth, desc.getUsername());
        final var hasUpdatedPassword = updatePassword(auth, desc.getPassword());

        return hasUpdatedUsername || hasUpdatedPassword;
    }

    /**
     * @param auth     The entity to be updated.
     * @param password The new password of the authentication.
     * @return True, if password is updated.
     */
    private boolean updatePassword(Authentication auth, String password) {
        final var newPassword = MetadataUtils.updateString(auth.getPassword(), password,
                "password");
        newPassword.ifPresent(auth::setPassword);
        return newPassword.isPresent();

    }

    /**
     * @param auth     The entity to be updated.
     * @param username The new username of the authentication.
     * @return True, if username is updated.
     */
    private boolean updateUsername(final Authentication auth, final String username) {
        final var newUsername = MetadataUtils.updateString(auth.getUsername(), username,
                "username");
        newUsername.ifPresent(auth::setUsername);
        return newUsername.isPresent();
    }
}
