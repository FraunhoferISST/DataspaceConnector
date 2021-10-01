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
package io.dataspaceconnector.extension.monitoring;

import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Contributor, adding IDS-Cert expiration to actuator info endpoint.
 */
@Component
@RequiredArgsConstructor
public class CertExpirationInfoContributor implements InfoContributor {

    /**
     * The Configuration Container.
     */
    private final ConfigContainer configContainer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void contribute(final Info.Builder builder) {
        final var info = new HashMap<String, Object>();
        final var expiration = configContainer.getKeyStoreManager().getCertExpiration();
        info.put("certExpiration", expiration);
        builder.withDetail("ids", info);
    }
}
