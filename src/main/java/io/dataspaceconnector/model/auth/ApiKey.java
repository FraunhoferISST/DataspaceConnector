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
package io.dataspaceconnector.model.auth;

import io.dataspaceconnector.common.net.HttpService.HttpArgs;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.HashMap;

import static io.dataspaceconnector.model.config.DatabaseConstants.AUTH_KEY_LENGTH;
import static io.dataspaceconnector.model.config.DatabaseConstants.AUTH_VALUE_LENGTH;

/**
 * Entity used for containing Basic Auth information in the context of AuthTypes.
 */
@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SQLDelete(sql = "UPDATE authentication SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ApiKey extends Authentication {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The key associated to the ApiKey.
     */
    @NonNull
    @Column(length = AUTH_KEY_LENGTH)
    private String key;

    /**
     * The value associated to the ApiKey.
     */
    @NonNull
    @Column(length = AUTH_VALUE_LENGTH)
    private String value;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuth(final HttpArgs args) {
        if (args.getHeaders() == null) {
            args.setHeaders(new HashMap<>());
        }

        args.getHeaders().put(key, value);
    }
}
