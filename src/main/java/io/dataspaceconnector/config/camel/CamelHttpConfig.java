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
package io.dataspaceconnector.config.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http.HttpComponent;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Configures the Camel HTTP component to use a custom truststore.
 */
@Configuration
public class CamelHttpConfig {

    /**
     * Location of the truststore to use.
     */
    @Value("${camel.truststore.path}")
    private String trustStoreLocation;

    /**
     * Password of the truststore to use.
     */
    @Value("${configuration.trustStorePassword}")
    private String trustStorePassword;

    /**
     * The Camel context.
     */
    private final CamelContext camelContext;

    /**
     * Constructs a CamelHttpConfig object using the given CamelContext.
     *
     * @param context the Camel context.
     */
    @Autowired
    public CamelHttpConfig(final CamelContext context) {
        this.camelContext = context;
    }

    /**
     * Creates a custom SSLContext using the truststore defined in application.properties
     * and configures the Camel HTTP component from the Camel context to use this custom SSL
     * context.
     *
     * @throws IOException              if an error occurs while loading the truststore.
     * @throws CertificateException     if an error occurs while loading the truststore.
     * @throws NoSuchAlgorithmException if an error occurs while loading the truststore.
     * @throws KeyStoreException        if an error occurs while loading the truststore.
     * @throws KeyManagementException   if the SSL context can not be built.
     */
    @PostConstruct
    public void configureTruststore() throws IOException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final var sslContext = SSLContexts.custom()
                .loadTrustMaterial(ResourceUtils.getURL(trustStoreLocation),
                        trustStorePassword.toCharArray())
                .build();

        final var socketFactory = new SSLConnectionSocketFactory(sslContext);
        final var socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("https", socketFactory)
                .build();

        final var connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        camelContext.getComponent("https", HttpComponent.class)
                .setClientConnectionManager(connectionManager);
    }

}
