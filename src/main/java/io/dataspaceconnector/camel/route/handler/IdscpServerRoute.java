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

import io.dataspaceconnector.camel.util.ParameterUtils;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Builds the route for receiving IDSCP_v2 messages. Incoming messages are parsed to
 * {@link io.dataspaceconnector.camel.dto.RouteMsg} DTOs and delegated to the appropriate message
 * handler route.
 */
@Component
@ConditionalOnProperty(name = "idscp2.enabled", havingValue = "true")
public class IdscpServerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @Override
    public void configure() throws Exception {
        from(ParameterUtils.IDSCP_SERVER_URI)
                .routeId("idscpServer")
                .process("TypeExtractionProcessor")
                .process("IncomingIdscpMessageParser")
                .choice()
                    .when(simple("${exchangeProperty.ids-type} == 'DescriptionRequestMessage'"))
                        .doTry()
                            .to("direct:descriptionRequestHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'DescriptionRequestMessageImpl'"))
                        .doTry()
                            .to("direct:descriptionRequestHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ContractRequestMessage'"))
                        .doTry()
                            .to("direct:contractRequestHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ContractRequestMessageImpl'"))
                        .doTry()
                            .to("direct:contractRequestHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ContractAgreementMessage'"))
                        .doTry()
                            .to("direct:contractAgreementHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ContractAgreementMessageImpl'"))
                        .doTry()
                            .to("direct:contractAgreementHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ArtifactRequestMessage'"))
                        .doTry()
                            .to("direct:artifactRequestHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ArtifactRequestMessageImpl'"))
                        .doTry()
                            .to("direct:artifactRequestHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'NotificationMessage'"))
                        .doTry()
                            .to("direct:notificationMsgHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'NotificationMessageImpl'"))
                        .doTry()
                            .to("direct:notificationMsgHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ResourceUpdateMessage'"))
                        .doTry()
                            .to("direct:resourceUpdateHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                    .when(simple("${exchangeProperty.ids-type} == 'ResourceUpdateMessageImpl'"))
                        .doTry()
                            .to("direct:resourceUpdateHandler")
                            .doCatch(Exception.class)
                        .end()
                    .endChoice()
                .end()
                .process("OutgoingIdscpMessageParser");
    }

}
