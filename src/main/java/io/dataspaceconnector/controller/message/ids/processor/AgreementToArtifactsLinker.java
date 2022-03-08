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
import io.dataspaceconnector.service.EntityUpdateService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Links the contract agreement received as the response to a ContractRequestMessage to the
 * artifacts created from the metadata received as the response to a DescriptionRequestMessage.
 */
@Component("AgreementToArtifactsLinker")
@RequiredArgsConstructor
public class AgreementToArtifactsLinker extends IdsResponseProcessor {

    /**
     * Service for updating database entities.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Links the contract agreement to the artifacts.
     *
     * @param exchange the exchange.
     * @throws Exception if linking the agreement to the artifacts fails.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws Exception {
        final var agreementId = exchange.getProperty(ParameterUtils.AGREEMENT_ID_PARAM, UUID.class);
        final var artifacts = exchange.getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class);

        updateService.linkArtifactToAgreement(toUriList(artifacts), agreementId);
    }

    @SuppressWarnings("unchecked")
    private static List<URI> toUriList(final List<?> list) {
        return (List<URI>) list;
    }
}
