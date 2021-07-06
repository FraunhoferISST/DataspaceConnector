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
package io.dataspaceconnector.config;

import java.net.URI;
import javax.annotation.PostConstruct;

import de.fhg.aisec.ids.camel.idscp2.Utils;
import de.fhg.aisec.ids.camel.idscp2.processors.ContractRequestCreationProcessor;
import de.fhg.aisec.ids.camel.idscp2.processors.IdsMessageTypeExtractionProcessor;
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
    public ContractRequestCreationProcessor contractRequestCreationProcessor() {
        return new ContractRequestCreationProcessor();
    }

    /**
     * Creates the SSL context used for IDSCP communication.
     *
     * @return the SSL context parameters.
     */
    @Bean
    public SSLContextParameters serverSslContext() {
        var ctx = new SSLContextParameters();
        ctx.setCertAlias("1");

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
