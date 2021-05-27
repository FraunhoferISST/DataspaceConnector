/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private boolean updatePassword(final Authentication auth, final String password) {
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
