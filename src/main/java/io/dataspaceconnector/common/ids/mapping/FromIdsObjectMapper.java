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
package io.dataspaceconnector.common.ids.mapping;

import de.fraunhofer.iais.eis.AppEndpoint;
import de.fraunhofer.iais.eis.AppRepresentation;
import de.fraunhofer.iais.eis.AppResource;
import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.DataApp;
import de.fraunhofer.iais.eis.PaymentModality;
import de.fraunhofer.iais.eis.Proxy;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.UsagePolicyClass;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.ids.policy.PolicyPattern;
import io.dataspaceconnector.common.time.TimeUtils;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.configuration.ConfigurationDesc;
import io.dataspaceconnector.model.configuration.ConnectorStatus;
import io.dataspaceconnector.model.configuration.DeployMode;
import io.dataspaceconnector.model.configuration.LogLevel;
import io.dataspaceconnector.model.configuration.SecurityProfile;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.endpoint.AppEndpointDesc;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.PaymentMethod;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.resource.ResourceDesc;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.template.AppEndpointTemplate;
import io.dataspaceconnector.model.template.AppTemplate;
import io.dataspaceconnector.model.template.ArtifactTemplate;
import io.dataspaceconnector.model.template.CatalogTemplate;
import io.dataspaceconnector.model.template.ContractTemplate;
import io.dataspaceconnector.model.template.RepresentationTemplate;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.model.template.RuleTemplate;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static io.dataspaceconnector.common.ids.mapping.AdditionalUtils.buildAdditionalForResource;

/**
 * Maps ids objects to internal entities.
 */
@Log4j2
public final class FromIdsObjectMapper {

    /**
     * Default constructor.
     */
    private FromIdsObjectMapper() {
        // not used
    }

    /**
     * Map ids catalog to connector catalog.
     *
     * @param catalog The ids catalog.
     * @return The connector catalog.
     * @throws IllegalArgumentException if the passed resource is null.
     */
    public static CatalogTemplate fromIdsCatalog(final Catalog catalog) {
        Utils.requireNonNull(catalog, ErrorMessage.ENTITY_NULL);

        final var additional = new HashMap<String, String>();
        if (catalog.getProperties() != null) {
            catalog.getProperties().forEach((key, value) -> additional.put(key, value.toString()));
        }

        final var catalogDesc = new CatalogDesc();
        catalogDesc.setAdditional(additional);
        catalogDesc.setTitle("IDS Catalog");
        catalogDesc.setDescription("This catalog is created from an IDS catalog.");
        catalogDesc.setBootstrapId(catalog.getId());

        return new CatalogTemplate(catalogDesc, null, null);
    }

    /**
     * Map ids app resource to resource.
     *
     * @param resource  The app resource.
     * @param remoteUrl The recipient id.
     * @return app template.
     */
    public static AppTemplate fromIdsApp(final AppResource resource, final URI remoteUrl) {
        Utils.requireNonNull(resource, ErrorMessage.ENTITY_NULL);

        final var description = resource.getDescription();
        final var keywords = MappingUtils.getKeywordsAsString(resource.getKeyword());
        final var language = resource.getLanguage();
        final var publisher = resource.getPublisher();
        final var sovereign = resource.getSovereign();
        final var standardLicense = resource.getStandardLicense();
        final var title = resource.getTitle();
        final var resourceEndpoint = resource.getResourceEndpoint();

        final var desc = new AppDesc();
        desc.setRemoteAddress(remoteUrl);
        desc.setRemoteId(resource.getId());

        // Set app resource attributes.
        desc.setAdditional(buildAdditionalForResource(resource));
        desc.setKeywords(keywords);
        desc.setPublisher(publisher);
        desc.setLicense(standardLicense);
        desc.setSovereign(sovereign);

        if (description != null && !description.isEmpty()) {
            desc.setDescription(description.size() == 1 ? description.get(0).getValue()
                    : description.toString());
        }

        if (title != null && !title.isEmpty()) {
            desc.setTitle(title.size() == 1 ? title.get(0).getValue() : title.toString());
        }

        if (language != null && !language.isEmpty()) {
            desc.setLanguage(language.size() == 1
                    ? language.get(0).toString() : language.toString());
        }

        if (resourceEndpoint != null && !resourceEndpoint.isEmpty()) {
            MappingUtils.getFirstEndpointDocumentation(resourceEndpoint)
                    .ifPresent(desc::setEndpointDocumentation);
        }

        var endpoints = new ArrayList<AppEndpointTemplate>();

        // Extract objects.
        if (resource.getRepresentation() != null) {
            final var representations = resource.getRepresentation().stream()
                    .filter(x -> x instanceof AppRepresentation)
                    .map(x -> (AppRepresentation) x)
                    .collect(Collectors.toList());
            if (!representations.isEmpty()) {
                // Note: Assume that an app resource has only one representation.
                final var representation = representations.get(0);

                // Set app representation attributes.
                fillAppWithRepresentationValues(representation, desc);

                if (representation.getDataAppInformation() != null) {
                    final var dataApp = representation.getDataAppInformation();

                    // Set data app attributes.
                    endpoints = fillAppWithDataAppValues(dataApp, desc);
                }
            }
        }

        // Set empty data initially.
        desc.setValue("");

        return new AppTemplate(desc, endpoints);
    }

    private static void fillAppWithRepresentationValues(final AppRepresentation representation,
                                                        final AppDesc desc) {
        final var distribution = representation.getDataAppDistributionService();
        final var runtimeEnvironment = representation.getDataAppRuntimeEnvironment();

        if (distribution != null) {
            desc.setDistributionService(distribution);
        }

        if (runtimeEnvironment != null) {
            desc.setRuntimeEnvironment(runtimeEnvironment);
        }

        desc.setAdditional(AdditionalUtils.buildAdditionalForAppRepresentation(representation));
    }

    private static ArrayList<AppEndpointTemplate> fillAppWithDataAppValues(final DataApp app,
                                                                           final AppDesc desc) {
        final var documentation = app.getAppDocumentation();
        final var envVariables = app.getAppEnvironmentVariables();
        final var storageConfig = app.getAppStorageConfiguration();
        final var supportedPolicies = app.getSupportedUsagePolicies();

        if (documentation != null) {
            desc.setDocs(documentation);
        }

        if (envVariables != null) {
            desc.setEnvVariables(envVariables);
        }

        if (storageConfig != null) {
            desc.setStorageConfig(storageConfig);
        }

        if (supportedPolicies != null && !supportedPolicies.isEmpty()) {
            desc.setSupportedPolicies(fromIdsUsagePolicyClass(supportedPolicies));
        }

        final var endpoints = new ArrayList<AppEndpointTemplate>();
        for (final var endpoint : app.getAppEndpoint()) {
            endpoints.add(fromIdsAppEndpoint(endpoint));
        }

        return endpoints;
    }

    private static AppEndpointTemplate fromIdsAppEndpoint(final AppEndpoint endpoint) {
        final var port = endpoint.getAppEndpointPort();
        final var mediaType = endpoint.getAppEndpointMediaType();
        final var type = endpoint.getAppEndpointType();
        final var protocol = endpoint.getAppEndpointProtocol();
        final var language = endpoint.getLanguage();
        final var endpointDocs = endpoint.getEndpointDocumentation();
        final var endpointInfo = endpoint.getEndpointInformation();
        final var path = endpoint.getPath();

        final var desc = new AppEndpointDesc();

        if (port != null) {
            desc.setEndpointPort(port.intValue());
        }

        if (mediaType != null) {
            desc.setMediaType(mediaType.getFilenameExtension());
        }

        if (type != null) {
            desc.setEndpointType(type.name());
        }

        if (language != null) {
            desc.setLanguage(language.toString());
        }

        if (protocol != null) {
            desc.setProtocol(protocol);
        }

        if (endpointDocs != null && !endpointDocs.isEmpty()) {
            desc.setDocs(endpointDocs.get(0));
        }

        if (endpointInfo != null && !endpointInfo.isEmpty()) {
            desc.setInfo(endpointInfo.size() == 1 ? endpointInfo.get(0).getValue()
                    : endpointInfo.toString());
        }

        if (path != null && !path.equals("")) {
            desc.setLocation(URI.create(path));
        }

        final var additional = AdditionalUtils.buildAdditionalForAppEndpoint(endpoint);
        desc.setAdditional(additional);

        return new AppEndpointTemplate(desc);
    }

    private static void fillResourceDesc(final ResourceDesc desc, final Resource resource) {
        final var description = resource.getDescription();
        final var keywords = MappingUtils.getKeywordsAsString(resource.getKeyword());
        final var language = resource.getLanguage();
        final var publisher = resource.getPublisher();
        final var sovereign = resource.getSovereign();
        final var standardLicense = resource.getStandardLicense();
        final var title = resource.getTitle();
        final var resourceEndpoint = resource.getResourceEndpoint();
        final var paymentModality = resource.getPaymentModality();
        final var samples = resource.getSample();

        desc.setAdditional(buildAdditionalForResource(resource));
        desc.setKeywords(keywords);
        desc.setPublisher(publisher);
        desc.setLicense(standardLicense);
        desc.setSovereign(sovereign);

        if (description != null && !description.isEmpty()) {
            desc.setDescription(description.size() == 1 ? description.get(0).getValue()
                    : description.toString());
        }

        if (title != null && !title.isEmpty()) {
            desc.setTitle(title.size() == 1 ? title.get(0).getValue() : title.toString());
        }

        if (language != null && !language.isEmpty()) {
            desc.setLanguage(
                    language.size() == 1 ? language.get(0).toString() : language.toString());
        }

        if (resourceEndpoint != null && !resourceEndpoint.isEmpty()) {
            MappingUtils.getFirstEndpointDocumentation(resourceEndpoint)
                    .ifPresent(desc::setEndpointDocumentation);
        }

        if (paymentModality != null) {
            desc.setPaymentMethod(fromIdsPaymentModality(paymentModality, resource.getId()));
        }

        if (samples != null && !samples.isEmpty()) {
            final var sampleUris = new ArrayList<URI>();
            for (final var sample : samples) {
                sampleUris.add(sample.getId());
            }
            desc.setSamples(sampleUris);
        }
    }

    /**
     * Map ids resource to connector resource.
     *
     * @param resource The ids resource.
     * @return The connector resource.
     * @throws IllegalArgumentException if the passed resource is null.
     */
    public static ResourceTemplate<OfferedResourceDesc> fromIdsOfferedResource(
            final Resource resource) {
        Utils.requireNonNull(resource, ErrorMessage.ENTITY_NULL);

        final var desc = new OfferedResourceDesc();
        fillResourceDesc(desc, resource);

        return new ResourceTemplate<>(desc);
    }

    /**
     * Map ids resource to connector resource.
     *
     * @param resource The ids resource.
     * @return The connector resource.
     * @throws IllegalArgumentException if the passed resource is null.
     */
    public static ResourceTemplate<RequestedResourceDesc> fromIdsResource(final Resource resource) {
        Utils.requireNonNull(resource, ErrorMessage.ENTITY_NULL);

        final var desc = new RequestedResourceDesc();
        desc.setRemoteId(resource.getId());
        fillResourceDesc(desc, resource);

        return new ResourceTemplate<>(desc);
    }

    /**
     * Map ids representation to connector representation.
     *
     * @param representation The ids representation.
     * @return The connector representation.
     * @throws IllegalArgumentException if the passed representation is null.
     */
    public static RepresentationTemplate fromIdsRepresentation(
            final Representation representation) {
        Utils.requireNonNull(representation, ErrorMessage.ENTITY_NULL);

        final var created = representation.getCreated();
        final var representationId = representation.getId();
        final var language = representation.getLanguage();
        final var mediaType = representation.getMediaType();
        final var modified = representation.getModified();
        final var standard = representation.getRepresentationStandard();
        final var shape = representation.getShapesGraph();

        // Add additional properties to map.
        final var additional
                = AdditionalUtils.propertiesToAdditional(representation.getProperties());

        if (created != null) {
            additional.put("ids:created", created.toXMLFormat());
        }
        if (modified != null) {
            additional.put("ids:modified", modified.toXMLFormat());
        }

        if (shape != null) {
            additional.put("ids:shapesGraph", String.valueOf(shape));
        }

        final var desc = new RepresentationDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(representationId);

        if (standard != null) {
            desc.setStandard(String.valueOf(standard));
        }

        if (language != null) {
            desc.setLanguage(language.toString());
        }

        if (mediaType != null) {
            desc.setMediaType(mediaType.getFilenameExtension());
        }

        return new RepresentationTemplate(desc);
    }

    /**
     * Build template from ids artifact.
     *
     * @param artifact  The ids artifact.
     * @param download  Whether the artifact will be downloaded automatically.
     * @param remoteUrl The provider's url for receiving artifact request messages.
     * @return The artifact template.
     * @throws IllegalArgumentException if the passed artifact is null.
     */
    public static ArtifactTemplate fromIdsArtifact(final Artifact artifact,
                                                   final boolean download, final URI remoteUrl) {
        Utils.requireNonNull(artifact, ErrorMessage.ENTITY_NULL);

        final var artifactId = artifact.getId();
        final var byteSize = artifact.getByteSize();
        final var checksum = artifact.getCheckSum();
        final var created = artifact.getCreationDate();
        final var duration = artifact.getDuration();
        final var filename = artifact.getFileName();

        // Add additional properties to map.
        final var additional = AdditionalUtils.propertiesToAdditional(artifact.getProperties());

        if (byteSize != null) {
            additional.put("ids:byteSize", byteSize.toString());
        }

        if (checksum != null) {
            additional.put("ids:checkSum", checksum);
        }

        if (created != null) {
            additional.put("ids:creationDate", created.toXMLFormat());
        }

        if (duration != null) {
            additional.put("ids:duration", duration.toString());
        }

        final var desc = new ArtifactDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(artifactId);
        desc.setTitle(filename);
        desc.setAutomatedDownload(download);
        desc.setRemoteAddress(remoteUrl);
        if (artifactId != null) {
            desc.setBootstrapId(URI.create(artifactId.toString()));
        }

        return new ArtifactTemplate(desc);
    }

    /**
     * Build template from ids contract.
     *
     * @param contract The ids contract offer.
     * @return The contract template.
     * @throws IllegalArgumentException if the passed contract is null.
     */
    public static ContractTemplate fromIdsContract(final Contract contract) {
        Utils.requireNonNull(contract, ErrorMessage.ENTITY_NULL);

        final var consumer = contract.getConsumer();
        final var date = contract.getContractDate();
        final var end = contract.getContractEnd();
        final var contractId = contract.getId();
        final var provider = contract.getProvider();
        final var start = contract.getContractStart();

        // Add additional properties to map.
        final var additional = AdditionalUtils.propertiesToAdditional(contract.getProperties());

        if (date != null) {
            additional.put("ids:contractDate", date.toXMLFormat());
        }

        final var desc = new ContractDesc();
        desc.setAdditional(additional);
        desc.setConsumer(consumer);
        desc.setProvider(provider);
        desc.setRemoteId(contractId);

        if (end != null) {
            try {
                desc.setEnd(TimeUtils.getDateOf(end.toXMLFormat()));
            } catch (DateTimeParseException ignored) {
                // Default values don't need to be set here.
            }
        }

        if (start != null) {
            try {
                desc.setStart(TimeUtils.getDateOf(start.toXMLFormat()));
            } catch (DateTimeParseException ignored) {
                // Default values don't need to be set here.
            }
        }

        return new ContractTemplate(desc);
    }

    /**
     * Build template from ids rule.
     *
     * @param rule The ids rule.
     * @return The rule template.
     * @throws IllegalArgumentException                                   if the rule is null.
     * @throws io.dataspaceconnector.common.exception.RdfBuilderException if the rule cannot be
     *                                                                    converted to string.
     */
    public static RuleTemplate fromIdsRule(final Rule rule) {
        Utils.requireNonNull(rule, ErrorMessage.ENTITY_NULL);

        final var value = RdfConverter.toRdf(rule);
        final var desc = new ContractRuleDesc();
        desc.setRemoteId(rule.getId());
        desc.setValue(value);

        if (rule.getTitle() != null && !rule.getTitle().isEmpty()) {
            desc.setTitle(rule.getTitle().toString());
        }

        return new RuleTemplate(desc);
    }

    /**
     * Build internal configuration desc from ids configModel.
     *
     * @param configModel The ids configuration model.
     * @return The internal configuration desc.
     */
    public static ConfigurationDesc fromIdsConfig(final ConfigurationModel configModel) {
        final var desc = new ConfigurationDesc();

        final var connector = configModel.getConnectorDescription();
        if (!connector.getTitle().isEmpty()) {
            desc.setTitle(connector.getTitle().get(0).getValue());
        }
        if (!connector.getDescription().isEmpty()) {
            desc.setDescription(connector.getDescription().get(0).getValue());
        }
        desc.setDeployMode(fromIdsDeployMode(configModel.getConnectorDeployMode()));
        desc.setCurator(connector.getCurator());
        desc.setDefaultEndpoint(connector.getHasDefaultEndpoint().getAccessURL());
        desc.setInboundModelVersion(connector.getInboundModelVersion());
        desc.setOutboundModelVersion(connector.getOutboundModelVersion());
        desc.setKeystoreSettings(new KeystoreDesc(
                configModel.getKeyStore(),
                configModel.getKeyStorePassword(),
                configModel.getKeyStoreAlias()));
        desc.setLogLevel(fromIdsLogLevel(configModel.getConfigurationModelLogLevel()));
        desc.setMaintainer(connector.getMaintainer());
        desc.setProxySettings(fromIdsProxy(configModel.getConnectorProxy()));
        desc.setSecurityProfile(fromIdsSecurityProfile(connector.getSecurityProfile()));
        desc.setTruststoreSettings(new TruststoreDesc(
                configModel.getTrustStore(),
                configModel.getTrustStorePassword(),
                configModel.getTrustStoreAlias()));
        desc.setStatus(fromIdsConnectorStatus(configModel.getConnectorStatus()));
        desc.setConnectorId(connector.getId());

        return desc;
    }

    /**
     * Get dsc log level from ids log level.
     *
     * @param logLevel The ids log level.
     * @return The internal log level.
     */
    private static LogLevel fromIdsLogLevel(final de.fraunhofer.iais.eis.LogLevel logLevel) {
        if (logLevel == null) {
            return LogLevel.OFF;
        }

        switch (logLevel) {
            // Note: As the IDS Infomodel has less log levels than DSC, information will get lost.
            case MINIMAL_LOGGING:
                return LogLevel.WARN;
            case DEBUG_LEVEL_LOGGING:
                return LogLevel.DEBUG;
            default:
                return LogLevel.OFF;
        }
    }

    /**
     * Get dsc security profile from ids security profile.
     *
     * @param securityProfile The ids security profile.
     * @return The internal security profile.
     */
    private static SecurityProfile fromIdsSecurityProfile(
            final de.fraunhofer.iais.eis.SecurityProfile securityProfile) {
        if (securityProfile == null) {
            return SecurityProfile.BASE_SECURITY;
        }

        switch (securityProfile) {
            case TRUST_SECURITY_PROFILE:
                return SecurityProfile.TRUST_SECURITY;
            case TRUST_PLUS_SECURITY_PROFILE:
                return SecurityProfile.TRUST_PLUS_SECURITY;
            default:
                return SecurityProfile.BASE_SECURITY;
        }
    }

    /**
     * Get dsc connector status from ids connector status.
     *
     * @param status The ids connector status.
     * @return The internal connector status.
     */
    public static ConnectorStatus fromIdsConnectorStatus(
            final de.fraunhofer.iais.eis.ConnectorStatus status) {
        if (status == null) {
            return ConnectorStatus.FAULTY;
        }

        switch (status) {
            case CONNECTOR_ONLINE:
                return ConnectorStatus.ONLINE;
            case CONNECTOR_OFFLINE:
                return ConnectorStatus.OFFLINE;
            default:
                return ConnectorStatus.FAULTY;
        }
    }

    /**
     * Get dsc deploy mode from ids deploy mode.
     *
     * @param deployMode The ids deploy mode.
     * @return The internal deploy mode.
     */
    public static DeployMode fromIdsDeployMode(final ConnectorDeployMode deployMode) {
        return deployMode == ConnectorDeployMode.TEST_DEPLOYMENT
                ? DeployMode.TEST : DeployMode.PRODUCTIVE;
    }

    private static ProxyDesc fromIdsProxy(final List<Proxy> proxyList) {
        if (proxyList == null || proxyList.isEmpty()) {
            return null;
        }

        final var proxy = proxyList.get(0);
        final var auth = proxy.getProxyAuthentication();
        return new ProxyDesc(proxy.getProxyURI(), proxy.getNoProxy()
                .stream()
                .map(URI::toString)
                .collect(Collectors.toList()),
                new AuthenticationDesc(auth.getAuthUsername(), auth.getAuthPassword()));
    }

    private static PaymentMethod fromIdsPaymentModality(final Object modality, final URI id) {
        if (modality == null) {
            return PaymentMethod.UNDEFINED;
        }

        try {
            switch ((PaymentModality) modality) {
                case FREE:
                    return PaymentMethod.FREE;
                case FIXED_PRICE:
                    return PaymentMethod.FIXED_PRICE;
                case NEGOTIATION_BASIS:
                    return PaymentMethod.NEGOTIATION_BASIS;
                default:
                    return PaymentMethod.UNDEFINED;
            }
        } catch (ClassCastException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Could not read payment modality from incoming resource. "
                        + "[resourceId=({}), modality=({})]", id, modality.toString());
            }
            return PaymentMethod.UNDEFINED;
        }
    }

    private static List<PolicyPattern> fromIdsUsagePolicyClass(final List<UsagePolicyClass> list) {
        var patterns = new ArrayList<PolicyPattern>();
        for (final var policyClass : list) {
            switch (policyClass) {
                case CONNECTOR_RESTRICTED_DATA_USAGE:
                    patterns.add(PolicyPattern.CONNECTOR_RESTRICTED_USAGE);
                    break;
                case DURATION_RESTRICTED_DATA_USAGE:
                    patterns.add(PolicyPattern.DURATION_USAGE);
                    break;
                case INTERVAL_RESTRICTED_DATA_USAGE:
                    patterns.add(PolicyPattern.USAGE_DURING_INTERVAL);
                    break;
                case LOCAL_LOGGING:
                    patterns.add(PolicyPattern.USAGE_LOGGING);
                    break;
                case ALLOW_DATA_USAGE:
                    patterns.add(PolicyPattern.PROVIDE_ACCESS);
                    break;
                case REMOTE_NOTIFICATION:
                    patterns.add(PolicyPattern.USAGE_NOTIFICATION);
                    break;
                case RESTRICTED_NUMBER_OF_USAGES:
                    patterns.add(PolicyPattern.N_TIMES_USAGE);
                    break;
                case USE_DATA_AND_DELETE_AFTER:
                    patterns.add(PolicyPattern.USAGE_UNTIL_DELETION);
                    break;
                default:
                    patterns.add(PolicyPattern.PROHIBIT_ACCESS);
                    break;
            }
        }

        return patterns;
    }
}
