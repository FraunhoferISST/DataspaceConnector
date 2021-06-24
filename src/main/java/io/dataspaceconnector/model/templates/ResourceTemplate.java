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
package io.dataspaceconnector.model.templates;

import java.net.URI;
import java.util.List;

import io.dataspaceconnector.model.base.AbstractDescription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Describes a resource and all its dependencies.
 * @param <D> The resource type.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ResourceTemplate<D extends AbstractDescription<?>> {

    /**
     * Old remote id.
     */
    private URI oldRemoteId;

    /**
     * Resource parameters.
     */
    @Setter(AccessLevel.NONE)
    private @NonNull D desc;

    /**
     * List of representation templates.
     */
    private List<RepresentationTemplate> representations;

    /**
     * List of contract templates.
     */
    private List<ContractTemplate> contracts;
}
