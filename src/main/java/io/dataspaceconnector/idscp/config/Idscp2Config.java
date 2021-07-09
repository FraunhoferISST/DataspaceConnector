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
package io.dataspaceconnector.idscp.config;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;

import de.fhg.aisec.ids.camel.idscp2.Utils;
import de.fhg.aisec.ids.camel.idscp2.processors.IdsMessageTypeExtractionProcessor;
import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import org.apache.camel.Processor;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contains configuration required for using IDSCP for communication.
 */
@Configuration
public class Idscp2Config {

    /**
     * Processor required for determining the type of an IDS message in a Camel route.
     *
     * @return the processor.
     */
    @Bean("TypeExtractionProcessor")
    public IdsMessageTypeExtractionProcessor idsMessageTypeExtractionProcessor() {
        return new IdsMessageTypeExtractionProcessor();
    }

    /**
     * Processor that creates a ContractRequestMessage in a Camel route.
     *
     * @return the processor.
     */
    @Bean("ContractRequestCreationProcessor")
    public Processor contractRequestCreationProcessor() {
        return exchange -> {
            final var in = exchange.getIn();

            final var message = new ContractRequestMessageBuilder()
                    ._issued_(IdsMessageUtils.getGregorianNow())
                    ._modelVersion_("4.1.0")
                    ._issuerConnector_(URI.create("https://some-connector.com"))
                    ._senderAgent_(URI.create("https://some-connector.com"))
                    ._securityToken_(new DynamicAttributeTokenBuilder()
                            ._tokenFormat_(TokenFormat.JWT)
                            ._tokenValue_("DAT")
                            .build())
                    ._recipientConnector_(Util.asList(URI.create("https://some-connector.com")))
                    .build();
            in.setHeader("idscp2-header", message);

            final var contractRequest = new ContractRequestBuilder(URI.create("https://some-contract-request.com"))
                    ._permission_(Util.asList(new PermissionBuilder()
                            ._action_(Util.asList(Action.USE))
                            ._target_(URI.create("https://some-artifact.com"))
                            .build()))
                    .build();
            in.setBody(contractRequest.toRdf().getBytes(StandardCharsets.UTF_8));
        };
    }

    /**
     * Creates the SSL context used for IDSCP communication.
     *
     * @return the SSL context parameters.
     */
    @Bean
    public SSLContextParameters serverSslContext() {
        var ctx = new SSLContextParameters();
        ctx.setCertAlias("1.0.1");

        final var keyStoreParameters = new KeyStoreParameters();
        keyStoreParameters.setResource("./src/main/resources/conf/cert.p12");
        keyStoreParameters.setPassword("password"); //TODO use properties

        final var keyManagers = new KeyManagersParameters();
        keyManagers.setKeyStore(keyStoreParameters);
        ctx.setKeyManagers(keyManagers);

        final var trustStoreParameters = new KeyStoreParameters();
        trustStoreParameters.setResource("./src/main/resources/conf/truststore.p12");
        trustStoreParameters.setPassword("password");

        final var trustManagers = new TrustManagersParameters();
        trustManagers.setKeyStore(trustStoreParameters);
        ctx.setTrustManagers(trustManagers);

        return ctx;
    }

    /**
     * Initializes the configuration for IDSCP.
     */
    @PostConstruct
    public void initConfig() {
        Utils.INSTANCE.setMaintainerUrlProducer(() -> URI.create("https://connector.com"));

        Utils.INSTANCE.setConnectorUrlProducer(() -> URI.create("https://connector.com"));

        Utils.INSTANCE.setInfomodelVersion("4.0.4");

        Utils.INSTANCE.setDapsUrlProducer(() -> "https://daps.aisec.fraunhofer.de");
    }

}
