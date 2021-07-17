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
package io.dataspaceconnector.config.interceptor;

import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.BasicAuthentication;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.iais.eis.Proxy;
import de.fraunhofer.iais.eis.ProxyBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.model.configuration.ConfigurationDesc;
import io.dataspaceconnector.model.configuration.DeployMode;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import org.hibernate.Hibernate;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to translate between infomodel and dsc configurations.
 */
public final class ConfigurationMapper {
    /**
     * Empty private constructor to avoid creating instances of this mapper.
     */
    private ConfigurationMapper() { }
    /**
     * Map DSC Configuration to Infomodel ConfigurationModel.
     *
     * @param configuration DSC configuration object
     * @return matching infomodel ConfigurationModel
     */
    public static ConfigurationModel buildInfomodelConfig(final Configuration configuration) {
        //TODO unmapped: configuration.getVersion()
        //TODO: keystore/truststore don't have alias fields
        if (configuration.getProxy() != null) {
            Hibernate.initialize(configuration.getProxy().getExclusions());
        }
        return new ConfigurationModelBuilder()
                ._connectorProxy_(buildDscProxy(configuration.getProxy()))
                ._connectorDeployMode_(buildInfomodelDeployMode(configuration.getDeployMode()))
                ._keyStore_(configuration.getKeystore().getLocation())
                ._keyStorePassword_(configuration.getKeystore().getPassword())
                ._keyStoreAlias_("")
                ._trustStore_(configuration.getTruststore().getLocation())
                ._trustStorePassword_(configuration.getTruststore().getPassword())
                ._trustStoreAlias_("")
                ._configurationModelLogLevel_(buildInfomodelLogLevel(configuration.getLogLevel()))
                ._connectorStatus_(
                        configuration.getStatus() != null
                                ? configuration.getStatus()
                                : ConnectorStatus.CONNECTOR_ONLINE
                )
                ._connectorDescription_(buildInfomodelConnector(configuration))
                .build();
    }

    /**
     * Map DSC to Infomodel proxy.
     *
     * @param proxy DSC Proxy object
     * @return Infomodel Proxy List (with one element)
     */
    private static List<Proxy> buildDscProxy(final io.dataspaceconnector.model.proxy.Proxy proxy) {
        if (proxy == null) {
            return null;
        }
        return List.of(new ProxyBuilder()
                ._noProxy_(
                        proxy.getExclusions().stream()
                                .map(URI::create)
                                .collect(Collectors.toList())
                )
                ._proxyAuthentication_(buildInfomodelAuth(proxy.getAuthentication()))
                ._proxyURI_(proxy.getLocation())
                .build());
    }

    /**
     * Map infomodel proxy to dsc proxy.
     *
     * @param proxyList Infomodel proxy list
     * @return DSC ProxyDescription object
     */
    private static ProxyDesc buildDscProxy(final List<Proxy> proxyList) {
        if (proxyList == null || proxyList.isEmpty()) {
            return null;
        }
        var proxy = proxyList.get(0);
        var auth = proxy.getProxyAuthentication();
        return new io.dataspaceconnector.model.proxy.ProxyDesc(
                        proxy.getProxyURI(),
                        proxy.getNoProxy()
                        .stream()
                        .map(URI::toString)
                        .collect(Collectors.toList()),
                new BasicAuth(auth.getAuthUsername(), auth.getAuthPassword()));

    }

    /**
     * Map DSC loglevel to Infomodel loglevel.
     *
     * @param logLevel DSC loglevel
     * @return mapped infomodel loglevel
     */
    private static LogLevel buildInfomodelLogLevel(
            final io.dataspaceconnector.model.configuration.LogLevel logLevel
    ) {
        switch (logLevel) {
            //TODO infomodel has less log levels than DSC, info will get lost
            case OFF: return LogLevel.NO_LOGGING;
            case INFO: return LogLevel.MINIMAL_LOGGING;
            case WARN: return LogLevel.MINIMAL_LOGGING;
            case DEBUG: return LogLevel.DEBUG_LEVEL_LOGGING;
            case ERROR: return LogLevel.MINIMAL_LOGGING;
            case TRACE: return LogLevel.MINIMAL_LOGGING;
            default: return LogLevel.NO_LOGGING;
        }
    }

    /**
     * Map infomodel loglevel to DSC loglevel.
     *
     * @param logLevel infomodel loglevel
     * @return dsc loglevel
     */
    private static io.dataspaceconnector.model.configuration.LogLevel buildDSCLogLevel(
            final LogLevel logLevel
    ) {
        switch (logLevel) {
            //TODO infomodel has less log levels than DSC, info will get lost
            case NO_LOGGING:
                return io.dataspaceconnector.model.configuration.LogLevel.OFF;
            case MINIMAL_LOGGING:
                return io.dataspaceconnector.model.configuration.LogLevel.WARN;
            case DEBUG_LEVEL_LOGGING:
                return io.dataspaceconnector.model.configuration.LogLevel.DEBUG;
            default:
                return io.dataspaceconnector.model.configuration.LogLevel.OFF;
        }
    }

    /**
     * Map DSC to Infomodel authentication.
     *
     * @param authentication DSC Authentication object
     * @return Infomodel BasicAuthentication
     */
    private static BasicAuthentication buildInfomodelAuth(final BasicAuth authentication) {
        //TODO auth from DSC has ID field, not available in Infomodel
        return new BasicAuthenticationBuilder()
                ._authPassword_(authentication.getPassword())
                ._authUsername_(authentication.getUsername())
                .build();
    }

    /**
     * Map DSC to Infomodel deploymode.
     *
     * @param deployMode DSC deployMode
     * @return Infomodel connectorDeployMode
     */
    private static ConnectorDeployMode buildInfomodelDeployMode(final DeployMode deployMode) {
        return deployMode == DeployMode.TEST ? ConnectorDeployMode.TEST_DEPLOYMENT
                : ConnectorDeployMode.PRODUCTIVE_DEPLOYMENT;
    }

    /**
     * Map DSC to Infomodel securityprofile.
     *
     * @param securityProfile DSC securityprofile
     * @return Infomodel securityprofile
     */
    private static SecurityProfile buildInfomodelSecurityProfile(
            final io.dataspaceconnector.model.configuration.SecurityProfile securityProfile
    ) {
            switch (securityProfile) {
                case BASE_SECURITY: return SecurityProfile.BASE_SECURITY_PROFILE;
                case TRUST_SECURITY: return SecurityProfile.TRUST_SECURITY_PROFILE;
                case TRUST_PLUS_SECURITY: return SecurityProfile.TRUST_PLUS_SECURITY_PROFILE;
                default: return SecurityProfile.BASE_SECURITY_PROFILE;
            }
    }

    /**
     * Map Infomodel to DSC SecurityProfile.
     *
     * @param securityProfile Infomodel SecurityProfile
     * @return DSC SecurityProfile
     */
    private static io.dataspaceconnector.model.configuration.SecurityProfile
    buildDSCSecurityProfile(
            final SecurityProfile securityProfile
    ) {
        switch (securityProfile) {
            case BASE_SECURITY_PROFILE:
                return io.dataspaceconnector.model.configuration.SecurityProfile.BASE_SECURITY;
            case TRUST_SECURITY_PROFILE:
                return io.dataspaceconnector.model.configuration.SecurityProfile.TRUST_SECURITY;
            case TRUST_PLUS_SECURITY_PROFILE:
                return io.dataspaceconnector.model.configuration.SecurityProfile.
                        TRUST_PLUS_SECURITY;
            default:
                return io.dataspaceconnector.model.configuration.SecurityProfile.BASE_SECURITY;
        }
    }

    /**
     * Map DSC configuration to Infomodel connector.
     *
     * @param configuration DSC configuration.
     * @return Infomodel Connector object.
     */
    private static Connector buildInfomodelConnector(final Configuration configuration) {
        return new BaseConnectorBuilder()
                ._title_(Util.asList(
                        new TypedLiteral(configuration.getTitle())))
                ._description_(Util.asList(
                        new TypedLiteral(configuration.getDescription())))
                ._curator_(configuration.getCurator())
                ._maintainer_(configuration.getMaintainer())
                ._securityProfile_(
                        buildInfomodelSecurityProfile(configuration.getSecurityProfile())
                )
                ._hasDefaultEndpoint_(
                        new ConnectorEndpointBuilder()
                                ._accessURL_(configuration.getConnectorEndpoint())
                                .build()
                )
                ._outboundModelVersion_(configuration.getOutboundModelVersion())
                ._inboundModelVersion_(configuration.getInboundModelVersion())
                .build();
    }

    /**
     * Map Infomodel to DSC Configuration.
     *
     * @param configurationModel Infomodel ConfigModel
     * @return DSC ConfigurationDescription
     */
    public static ConfigurationDesc buildConfigDesc(final ConfigurationModel configurationModel) {
        var description = new ConfigurationDesc();
        if (!configurationModel.getConnectorDescription().getTitle().isEmpty()) {
            description.setTitle(
                    configurationModel.getConnectorDescription().getTitle().get(0).getValue());
        }
        if (!configurationModel.getConnectorDescription().getDescription().isEmpty()) {
            description.setDescription(
                    configurationModel.getConnectorDescription().getDescription().get(0).getValue()
            );
        }
        description.setSelected(CurrentConfig.SELECTED);
        description.setDeployMode(
                configurationModel
                        .getConnectorDeployMode() == ConnectorDeployMode.TEST_DEPLOYMENT
                        ? DeployMode.TEST : DeployMode.PRODUCTIVE
        );
        description.setCurator(
                configurationModel.getConnectorDescription().getCurator()
        );
        description.setConnectorEndpoint(
                configurationModel.getConnectorDescription().getHasDefaultEndpoint().getAccessURL()
        );
        description.setInboundModelVersion(
                configurationModel.getConnectorDescription().getInboundModelVersion()
        );
        description.setOutboundModelVersion(
                configurationModel.getConnectorDescription().getOutboundModelVersion()
        );
        description.setKeystoreSettings(
                new KeystoreDesc(
                        configurationModel.getKeyStore(),
                        configurationModel.getKeyStorePassword()
                )
        );
        description.setLogLevel(
                buildDSCLogLevel(configurationModel.getConfigurationModelLogLevel())
        );
        description.setMaintainer(
                configurationModel.getConnectorDescription().getMaintainer()
        );
        description.setProxySettings(
                buildDscProxy(configurationModel.getConnectorProxy())
        );
        description.setSecurityProfile(
                buildDSCSecurityProfile(
                        configurationModel.getConnectorDescription().getSecurityProfile()
                )
        );
        description.setTruststoreSettings(
                new TruststoreDesc(
                        configurationModel.getTrustStore(),
                        configurationModel.getTrustStorePassword()
                )
        );
        //description.setVersion(null);
        return description;
    }


}
