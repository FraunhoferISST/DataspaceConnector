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
import de.fraunhofer.ids.messaging.core.daps.DapsValidator;
import de.fraunhofer.ids.messaging.core.daps.TokenProviderService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
public class IdsInfoContributor implements InfoContributor {

    /**
     * The Configuration Container.
     */
    private final ConfigContainer configContainer;

    /**
     * The Tokenprovider.
     */
    private final TokenProviderService tokenProvSvc;

    /**
     * {@inheritDoc}
     */
    @Override
    public void contribute(final Info.Builder builder) {
        addValidDatInfo(builder);
        addConnectorInfo(builder);
    }

    private void addConnectorInfo(final Info.Builder builder) {
        final var conInfo = new HashMap<String, Object>();
        final var expiration = configContainer.getKeyStoreManager().getCertExpiration();
        conInfo.put("connectorCertExpiration", expiration);
        final var inbound = configContainer.getConnector().getInboundModelVersion();
        conInfo.put("inboundModelVersion", inbound);
        final var outbound = configContainer.getConnector().getOutboundModelVersion();
        conInfo.put("outboundModelVersion", outbound);
        final var deploy = configContainer.getConfigurationModel().getConnectorDeployMode().getId();
        conInfo.put("deployMode", deploy);
        final var status = configContainer.getConfigurationModel().getConnectorStatus().getId();
        conInfo.put("status", status);
        builder.withDetail("connector", conInfo);
    }

    @SuppressFBWarnings(value = "REC_CATCH_EXCEPTION",
            justification = "Catching all possible exceptions.")
    private void addValidDatInfo(final Info.Builder builder) {
        final var datInfo = new HashMap<String, Object>();
        try {
            final var dat = tokenProvSvc.getDAT();
            var claims = DapsValidator.getClaims(
                    dat,
                    tokenProvSvc.providePublicKeys()
            );
            final var exp = claims.getBody().getExpiration();
            datInfo.put("datExpiration", exp);
            final var iss = claims.getBody().getIssuer();
            datInfo.put("issuer", iss);
            final var issuedAt = claims.getBody().getIssuedAt();
            datInfo.put("issuedAt", issuedAt);
            final var audience = claims.getBody().getAudience();
            datInfo.put("audience", audience);
            builder.withDetail("dat", datInfo);
        } catch (Exception e) {
            builder.withDetail("dat", false);
        }
    }
}
