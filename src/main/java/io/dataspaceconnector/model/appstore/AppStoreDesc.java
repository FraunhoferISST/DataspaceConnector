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
package io.dataspaceconnector.model.appstore;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.base.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Describes a app store's properties.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppStoreDesc extends Description {

    /**
     * The access url of the app store.
     */
    private URI name;

    /**
     * The title of the app store.
     */
    private String title;

    /**
     * The registration status.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RegistrationStatus status;
}
