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

import kotlin.Pair;
import lombok.*;
import okhttp3.Credentials;

import javax.persistence.Entity;

/**
 * Entity used for containing Basic Auth information in the context of AuthTypes
 */
@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor
@NoArgsConstructor
public class BasicAuth extends AuthType{

    /**
     * The username that is to be used for Basic Auth
     */
    @NonNull
    private String username;
    /**
     * The password that is to be used for Basic Auth
     */
    @NonNull
    private String password;

    /**
     * Generates the HTTP header information for the HTTP Basic Auth
     * @return a Pair containing the key and value used as HTTP header
     */
    @Override
    public Pair<String, String> addAuth() {
        return new Pair<>("Authorization", Credentials.basic(username, password));
    }

}
