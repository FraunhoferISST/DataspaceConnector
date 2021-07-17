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
package io.dataspaceconnector.service.usagecontrol;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;

import io.dataspaceconnector.service.ids.ConnectorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Builds log messages for ids communication.
 */
@Component
@RequiredArgsConstructor
public class LogBuilder {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Build a log information object.
     * @param target The accessed element.
     * @return The log line.
     */
    public String buildLog(final URI target) {
        final var id = connectorService.getConnectorId();

        final var output = new HashMap<String, Object>();
        output.put("target", target);
        output.put("issuerConnector", id);
        output.put("accessed", ZonedDateTime.now(ZoneOffset.UTC));

        return output.toString();
    }
}
