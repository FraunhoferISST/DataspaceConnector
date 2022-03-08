/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.resource.templatebuilder;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.template.AppEndpointTemplate;
import io.dataspaceconnector.service.resource.type.AppEndpointService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds app endpoints from templates.
 */
@RequiredArgsConstructor
public class AppEndpointTemplateBuilder {

    /**
     * The service for app endpoints.
     */
    private final @NonNull AppEndpointService appEndpointService;

    /**
     * Build app endpoint from template.
     *
     * @param appEndpointTemplate The app endpoint template.
     * @return The new app endpoint.
     */
    public AppEndpoint build(final AppEndpointTemplate appEndpointTemplate) {
        Utils.requireNonNull(appEndpointTemplate, ErrorMessage.ENTITY_NULL);
        return appEndpointService.create(appEndpointTemplate.getDesc());
    }
}
