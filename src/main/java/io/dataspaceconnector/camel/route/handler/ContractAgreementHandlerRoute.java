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
package io.dataspaceconnector.camel.route.handler;

import io.dataspaceconnector.camel.exception.UnconfirmedAgreementException;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.MessageRequestException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for handling a ContractAgreementMessage.
 */
@Component
public class ContractAgreementHandlerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @Override
    public void configure() throws Exception {
        onException(MessageRequestException.class)
                .to("direct:handleMessagePayloadException");
        onException(ContractException.class)
                .to("direct:handleContractException");
        onException(UnconfirmedAgreementException.class)
                .to("direct:handleMessageProcessingFailedForAgreement");
        onException(ResourceNotFoundException.class)
                .to("direct:handleMessageProcessingFailedForAgreement");
        onException(IllegalArgumentException.class)
                .to("direct:handleIllegalArgumentException");

        from("direct:contractAgreementHandler")
                .routeId("contractAgreementHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("AgreementDeserializer")
                .process("AgreementComparisonProcessor");
    }

}
