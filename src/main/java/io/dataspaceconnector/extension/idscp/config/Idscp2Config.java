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
package io.dataspaceconnector.extension.idscp.config;

import de.fhg.aisec.ids.camel.idscp2.Utils;
import de.fhg.aisec.ids.camel.idscp2.processors.IdsMessageTypeExtractionProcessor;
import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.annotation.PostConstruct;

/**
 * Contains configuration required for using IDSCP for communication.
 */
@Configuration
@RequiredArgsConstructor
public class Idscp2Config {

    /**
     * Location of the keystore containing the IDS certificate.
     */
    @Value("${idscp2.keystore}")
    private String keyStoreLocation;

    /**
     * Password of the keystore containing the IDS certificate.
     */
    @Value("${configuration.keyStorePassword}")
    private String keyStorePassword;

    /**
     * Alias of the IDS certificate in the keystore.
     */
    @Value("${configuration.keyAlias}")
    private String keyStoreAlias;

    /**
     * Location of the truststore to use.
     */
    @Value("${idscp2.truststore}")
    private String trustStoreLocation;

    /**
     * Password of the truststore to use.
     */
    @Value("${configuration.trustStorePassword}")
    private String trustStorePassword;

    /**
     * Base URL of the DAPS.
     */
    @Value("${daps.url}")
    private String dapsUrl;

    /**
     * Transaction manager required for creating a transaction policy for Camel routes.
     */
    private final @NonNull TransactionManager transactionManager;

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigContainer configContainer;

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
     * Creates the SSL context used for IDSCP communication.
     *
     * @return the SSL context parameters.
     */
    @Bean
    public SSLContextParameters serverSslContext() {
        var ctx = new SSLContextParameters();
//        ctx.setCertAlias(keyStoreAlias); TODO only 1.0.1 works as alias
        ctx.setCertAlias("1.0.1");

        final var keyStoreParameters = new KeyStoreParameters();
        keyStoreParameters.setResource(keyStoreLocation);
        keyStoreParameters.setPassword(keyStorePassword);

        final var keyManagers = new KeyManagersParameters();
        keyManagers.setKeyStore(keyStoreParameters);
        ctx.setKeyManagers(keyManagers);

        final var trustStoreParameters = new KeyStoreParameters();
        trustStoreParameters.setResource(trustStoreLocation);
        trustStoreParameters.setPassword(trustStorePassword);

        final var trustManagers = new TrustManagersParameters();
        trustManagers.setKeyStore(trustStoreParameters);
        ctx.setTrustManagers(trustManagers);

        return ctx;
    }

    /**
     * Configures the transaction policy for routes.
     *
     * @return the transaction policy.
     */
    @Bean("transactionPolicy")
    public SpringTransactionPolicy springTransactionPolicy() {
        final var policy = new SpringTransactionPolicy();
        policy.setTransactionManager((PlatformTransactionManager) transactionManager);
        policy.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return policy;
    }

    /**
     * Initializes the configuration for IDSCP.
     */
    @PostConstruct
    public void initConfig() {
        final var connector = configContainer.getConnector();

        Utils.INSTANCE.setMaintainerUrlProducer(connector::getMaintainer);
        Utils.INSTANCE.setConnectorUrlProducer(connector::getId);
        Utils.INSTANCE.setInfomodelVersion(connector.getOutboundModelVersion());
        Utils.INSTANCE.setDapsUrlProducer(() -> dapsUrl);
    }

}
