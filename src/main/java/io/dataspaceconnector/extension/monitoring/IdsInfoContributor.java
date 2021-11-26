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

import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.ConnectorMissingCertExtensionException;
import de.fraunhofer.ids.messaging.core.daps.DapsConnectionException;
import de.fraunhofer.ids.messaging.core.daps.DapsEmptyResponseException;
import de.fraunhofer.ids.messaging.core.daps.DapsValidator;
import de.fraunhofer.ids.messaging.core.daps.TokenProviderService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * Contributor, adding IDS-Cert expiration to actuator info endpoint.
 */
@Component
@RequiredArgsConstructor
public class IdsInfoContributor implements InfoContributor {

    /**
     * Service for connector details.
     */
    private final ConnectorService connectorSvc;

    /**
     * The token provider.
     */
    private final TokenProviderService tokenProvSvc;

    /**
     * {@inheritDoc}
     */
    @Override
    public void contribute(final Info.Builder builder) {
        builder.withDetail("configuration", getConfigDetails());
        builder.withDetail("ids", getIdsDetails());

    }

    private Map<String, Object> getConfigDetails() {
        return new HashMap<>() {{
            put("deployMode", connectorSvc.getDeployMethod());
            put("connectorStatus", connectorSvc.getConnectorStatus());
        }};
    }

    @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
    private Map<String, Object> getIdsDetails() {
        final var map = new HashMap<String, Object>() {{
            put("infoModel", getInfoModelDetails());
            put("certificate", getCertDetails());
        }};

        try {
            map.put("dat", getDatDetails());
        } catch (ClaimsException | ConnectorMissingCertExtensionException
                | DapsConnectionException | DapsEmptyResponseException | NullPointerException e) {
            map.put("dat", false);
        }

        return map;
    }

    private Map<String, Object> getInfoModelDetails() {
        return new HashMap<>() {{
            put("inboundVersion", connectorSvc.getInboundModelVersion());
            put("outboundVersion", connectorSvc.getOutboundModelVersion());
        }};
    }

    @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
    private Map<String, Object> getCertDetails() {
        final var keyStoreManager = connectorSvc.getKeyStoreManager();

        final var map = new HashMap<String, Object>();

        try {
            map.put("expirationDate", keyStoreManager.getCertExpiration());

            final var cert = (X509Certificate) keyStoreManager.getCert();
            Utils.requireNonNull(cert, ErrorMessage.EMTPY_ENTITY);

            map.put("issuer", cert.getIssuerDN().getName());
            map.put("issuedAt", cert.getNotBefore());
            map.put("sigAlgName", cert.getSigAlgName());
            map.put("type", cert.getType());
            map.put("version", cert.getVersion());
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }

        return map;
    }

    private Map<String, Object> getDatDetails() throws ClaimsException, DapsConnectionException,
            ConnectorMissingCertExtensionException, DapsEmptyResponseException {
        final var dat = tokenProvSvc.getDAT();
        final var claims
                = DapsValidator.getClaims(dat, tokenProvSvc.providePublicKeys()).getBody();

        final var map = new HashMap<String, Object>();
        map.put("audience", claims.getAudience());
        map.put("expirationDate", claims.getExpiration());
        map.put("issuer", claims.getIssuer());
        map.put("issuedAt", claims.getIssuedAt());
        map.put("referringConnector", claims.get("referringConnector"));
        map.put("securityProfile", claims.get("securityProfile"));

        return map;
    }
}
