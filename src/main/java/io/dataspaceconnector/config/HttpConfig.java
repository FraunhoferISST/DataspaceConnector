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

import de.fraunhofer.ids.messaging.protocol.http.HttpService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * This class handles connection settings for outgoing http connections.
 */
@Configuration
@RequiredArgsConstructor
public class HttpConfig {

    /**
     * Global timeout value.
     */
    @Value("${http.timeout.connect}")
    private long connectTimeout;

    /**
     * Read timeout value.
     */
    @Value("${http.timeout.read}")
    private long readTimeout;

    /**
     * Write timeout value.
     */
    @Value("${http.timeout.write}")
    private long writeTimeout;

    /**
     * Call timeout value.
     */
    @Value("${http.timeout.call}")
    private long callTimeout;

    /**
     * Service for http connections.
     */
    private final @NonNull HttpService httpService;

    /**
     * Hand over connection settings from the application.properties to the http client. Either the
     * three values connect, read, and write are used, or the global call timeout.
     */
    @PostConstruct
    public void setTimeouts() {
        if (connectTimeout == 0 || readTimeout == 0 || writeTimeout == 0) {
            httpService.setTimeouts(null, null, null,
            Duration.ofMillis(callTimeout));
        } else {
            httpService.setTimeouts(
                    Duration.ofMillis(connectTimeout),
                    Duration.ofMillis(readTimeout),
                    Duration.ofMillis(writeTimeout),
                    null);
        }
    }
}
