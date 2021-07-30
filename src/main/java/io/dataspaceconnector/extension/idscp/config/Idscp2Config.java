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
import de.fhg.aisec.ids.cmc.CmcConfig;
import de.fhg.aisec.ids.cmc.prover.CmcProver;
import de.fhg.aisec.ids.cmc.prover.CmcProverConfig;
import de.fhg.aisec.ids.cmc.verifier.CmcVerifier;
import de.fhg.aisec.ids.cmc.verifier.CmcVerifierConfig;
import de.fhg.aisec.ids.idscp2.idscp_core.rat_registry.RatProverDriverRegistry;
import de.fhg.aisec.ids.idscp2.idscp_core.rat_registry.RatVerifierDriverRegistry;
import de.fhg.aisec.ids.tpm2d.TpmHelper;
import de.fhg.aisec.ids.tpm2d.messages.TpmAttestation;
import de.fhg.aisec.ids.tpm2d.tpm2d_prover.TpmProver;
import de.fhg.aisec.ids.tpm2d.tpm2d_prover.TpmProverConfig;
import de.fhg.aisec.ids.tpm2d.tpm2d_verifier.TpmVerifier;
import de.fhg.aisec.ids.tpm2d.tpm2d_verifier.TpmVerifierConfig;
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
import java.nio.file.Paths;

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
     * Hostname where tpm2d is found.
     */
    @Value("${idscp2.tpm2d-host}")
    private String tpm2dHost;

    /**
     * Location of TPM root certificate
     */
    @Value("${idscp2.tpm-root-certificate}")
    private String tpmRootCertificate;

    /**
     * Hostname where CMC is found.
     */
    @Value("${idscp2.cmc-host}")
    private String cmcHost;

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
//        ctx.setCertAlias(keyStoreAlias); Note: Only 1.0.1 works as alias
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

        idscp2TpmRatConfig();
        idscp2CmcRatConfig();
    }

    public void idscp2TpmRatConfig() {
        // RAT prover configuration
        var tpm2dHostAndPort = tpm2dHost.split(":");
        int tpm2dPort = TpmProverConfig.DEFAULT_TPM_PORT;
        if (tpm2dHostAndPort.length > 1) {
            tpm2dPort = Integer.parseInt(tpm2dHostAndPort[1]);
        }
        var proverConfig = new TpmProverConfig.Builder()
                .setTpmHost(tpm2dHostAndPort[0])
                .setTpmPort(tpm2dPort)
                .build();
        RatProverDriverRegistry.INSTANCE.registerDriver(
                TpmProver.ID, TpmProver::new, proverConfig
        );

        // RAT verifier configuration
        var verifierConfig =  new TpmVerifierConfig.Builder()
                .setLocalCertificate(
                        TpmHelper.INSTANCE.loadCertificateFromKeystore(
                                Paths.get(keyStoreLocation),
                                keyStorePassword.toCharArray(),
                                keyStoreAlias
                        )
                )
//                .addRootCaCertificates(Paths.get("/certs/tpm-truststore.p12"), "password".toCharArray())
//                .setExpectedAttestationType(TpmAttestation.IdsAttestationType.ALL)
                .addRootCaCertificateFromPem(Paths.get(tpmRootCertificate))
                .setExpectedAttestationType(TpmAttestation.IdsAttestationType.ADVANCED)
                .setExpectedAttestationMask(0x0603ff)
                .build();
        RatVerifierDriverRegistry.INSTANCE.registerDriver(
                TpmVerifier.ID, TpmVerifier::new, verifierConfig
        );
    }

    public void idscp2CmcRatConfig() {
        // RAT prover configuration
        var cmcHostAndPort = cmcHost.split(":");
        int cmcPort = CmcConfig.DEFAULT_CMC_PORT;
        if (cmcHostAndPort.length > 1) {
            cmcPort = Integer.parseInt(cmcHostAndPort[1]);
        }
        var proverConfig = new CmcProverConfig.Builder()
                .setCmcHost(cmcHostAndPort[0])
                .setCmcPort(cmcPort)
                .build();
        RatProverDriverRegistry.INSTANCE.registerDriver(
                CmcProver.ID, CmcProver::new, proverConfig
        );

        // RAT verifier configuration
        var verifierConfig =  new CmcVerifierConfig.Builder()
                .setCmcHost(cmcHostAndPort[0])
                .setCmcPort(cmcPort)
                .build();
        RatVerifierDriverRegistry.INSTANCE.registerDriver(
                CmcVerifier.ID, CmcVerifier::new, verifierConfig
        );
    }

}
