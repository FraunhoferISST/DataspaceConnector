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
package io.dataspaceconnector.controller.message.ids.processor;

import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.controller.message.ids.processor.base.IdsResponseProcessor;
import io.dataspaceconnector.service.EntityPersistenceService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.util.ProcessorUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * Persists the metadata received as the response to a DescriptionRequestMessage.
 */
@Component("MetadataPersistenceProcessor")
@RequiredArgsConstructor
public class MetadataPersistenceProcessor extends IdsResponseProcessor {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Persists the metadata.
     *
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var response = exchange.getIn().getBody(Response.class);
        final var map = ProcessorUtils.getResponseMap(response);

        final var artifacts = exchange.getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class);
        final var download = exchange.getProperty(ParameterUtils.DOWNLOAD_PARAM, boolean.class);
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        persistenceSvc.saveMetadata(map, toUriList(artifacts), download, recipient);
    }

    @SuppressWarnings("unchecked")
    private static List<URI> toUriList(final List<?> list) {
        return (List<URI>) list;
    }
}
