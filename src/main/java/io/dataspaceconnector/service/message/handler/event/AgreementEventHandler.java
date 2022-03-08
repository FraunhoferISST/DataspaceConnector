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
package io.dataspaceconnector.service.message.handler.event;

import io.dataspaceconnector.service.message.handler.dto.payload.AgreementClaimsContainer;
import io.dataspaceconnector.service.usagecontrol.PolicyExecutionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Handles agreement events by sending the agreement to the clearing house.
 */
@Component
@RequiredArgsConstructor
public class AgreementEventHandler {

    /**
     * Policy execution point.
     */
    private final @NonNull PolicyExecutionService executionService;

    /**
     * Sends the agreement received in an event to the clearing house.
     *
     * @param agreementContainer contains the agreement and the requesting connector's claims.
     */
    @Async
    @EventListener
    public void handleAgreementEvent(final AgreementClaimsContainer agreementContainer) {
        executionService.sendAgreement(agreementContainer.getAgreement(),
                agreementContainer.getClaims());
    }

}
