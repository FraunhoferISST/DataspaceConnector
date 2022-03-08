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

import java.util.stream.Collectors;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.template.AppTemplate;
import io.dataspaceconnector.service.resource.relation.AppEndpointLinker;
import io.dataspaceconnector.service.resource.type.AppService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds apps from templates.
 */
@RequiredArgsConstructor
public class AppTemplateBuilder {

    /**
     * The service for apps.
     */
    private final @NonNull AppService appService;

    /**
     * The linker for app-endpoint relations.
     */
    private final @NonNull AppEndpointLinker appEndpointLinker;

    /**
     * Builder for app endpoints.
     */
    private final @NonNull AppEndpointTemplateBuilder endpointBuilder;

    /**
     * @param template The app template.
     * @return The new app.
     */
    public App build(final AppTemplate template) {
        Utils.requireNonNull(template, ErrorMessage.ENTITY_NULL);

        final var appEndpointIds = Utils.toStream(template.getEndpoints())
                                        .map(x -> endpointBuilder.build(x).getId())
                                        .collect(Collectors.toSet());

        final var appId =
                appService.identifyByRemoteId(template.getDesc().getRemoteId());
        final var app = appId.isPresent()
                ? appService.update(appId.get(), template.getDesc())
                : appService.create(template.getDesc());

        appEndpointLinker.add(app.getId(), appEndpointIds);

        return app;
    }
}
