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
package io.dataspaceconnector.camel.route.controller;

import java.io.IOException;
import java.net.SocketTimeoutException;
import javax.persistence.PersistenceException;

import de.fhg.aisec.ids.idscp2.idscp_core.error.Idscp2Exception;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.InvalidInputException;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for sending a contract request, a description request and an optional artifact
 * request over IDSCP_v2.
 */
@Component
public class ContractRequestControllerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void configure() throws Exception {
        onException(InvalidInputException.class)
                .to("direct:handleInvalidInputException");
        onException(ConstraintViolationException.class)
                .to("direct:handleFailedToBuildContractRequest");
        onException(InvalidResponseException.class)
                .to("direct:handleInvalidResponseException");
        onException(MessageException.class)
                .to("direct:handleIdsMessageFailed");
        onException(MessageResponseException.class, ContractException.class,
                IllegalArgumentException.class)
                .to("direct:handleReceivedInvalidResponse");
        onException(PersistenceException.class)
                .to("direct:handlePersistenceException");
        onException(IOException.class, ResourceNotFoundException.class,
                MessageResponseException.class)
                .log(LoggingLevel.DEBUG, "Could not save data for artifact. "
                        + "[artifact=(${exchangeProperty.currentArtifact}), "
                        + "exception=(${exception.message})]");
        onException(SocketTimeoutException.class)
                .to("direct:handleSocketTimeout");
        onException(Idscp2Exception.class)
                .to("direct:handleIdscp2Exception");

        from("direct:contractRequestSender")
                .routeId("contractRequestSender")
                .process("RuleListInputValidator")
                .process("ContractRequestMessageBuilder")
                .process("ContractRequestPreparer")
                .toD(ParameterUtils.IDSCP_CLIENT_URI)
                .process("ResponseToDtoConverter")
                .process("ContractResponseValidator")
                .process("ContractAgreementValidator")
                .process("ContractAgreementMessageBuilder")
                .process("ContractAgreementPreparer")
                .toD(ParameterUtils.IDSCP_CLIENT_URI)
                .process("ResponseToDtoConverter")
                .process("ContractAgreementResponseValidator")
                .process("ContractAgreementPersistenceProcessor")
                .loop(simple("${exchangeProperty.resources.size()}"))
                    .process("DescriptionRequestMessageBuilder")
                    .process("RequestWithoutPayloadPreparer")
                    .toD(ParameterUtils.IDSCP_CLIENT_URI)
                    .process("ResponseToDtoConverter")
                    .process("DescriptionResponseValidator")
                    .process("MetadataPersistenceProcessor")
                .end()
                .process("AgreementToArtifactsLinker")
                .choice()
                    .when(simple("${exchangeProperty.download}"))
                        .loop(simple("${exchangeProperty.artifacts.size()}"))
                            .process("ArtifactRequestMessageBuilder")
                            .process("RequestWithoutPayloadPreparer")
                            .toD(ParameterUtils.IDSCP_CLIENT_URI)
                            .process("ResponseToDtoConverter")
                            .process("ArtifactResponseValidator")
                            .process("DataPersistenceProcessor")
                        .end()
                    .endChoice()
                .end();


    }

}
