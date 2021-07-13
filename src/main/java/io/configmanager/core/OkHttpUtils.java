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
package io.configmanager.core;

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Utility Class for generating an OkHttpClient which does not validate Certificate Chains.
 */
@Log4j2
public final class OkHttpUtils {
    /**
     * Default timeout for HTTP-Requests.
     */
    private static final int TIMEOUT = 30;

    private OkHttpUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Static method for generating an OkHttpClient which does not validate Certificate Chains.
     *
     * @return unsafe OKHttpClient
     */
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final var trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(final X509Certificate[] chain,
                                                       final String authType) {
                        }

                        @Override
                        public void checkServerTrusted(final X509Certificate[] chain,
                                                       final String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final var sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final var sslSocketFactory = sslContext.getSocketFactory();

            final var builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);

            return builder.build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            if (log.isErrorEnabled()) {
                log.error("Error getting the SSL-Context: " + e.getCause());
            }

            throw new UnsupportedOperationException(e);
        }
    }
}
