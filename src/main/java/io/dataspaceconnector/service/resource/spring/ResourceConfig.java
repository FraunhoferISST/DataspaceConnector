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
package io.dataspaceconnector.service.resource.spring;

import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.model.agreement.AgreementFactory;
import io.dataspaceconnector.model.app.AppFactory;
import io.dataspaceconnector.model.appstore.AppStoreFactory;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.broker.BrokerFactory;
import io.dataspaceconnector.model.catalog.CatalogFactory;
import io.dataspaceconnector.model.configuration.ConfigurationFactory;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.model.datasource.DataSourceFactory;
import io.dataspaceconnector.model.endpoint.AppEndpointFactory;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointFactory;
import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.representation.RepresentationFactory;
import io.dataspaceconnector.model.resource.RequestedResourceFactory;
import io.dataspaceconnector.model.route.RouteFactory;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import io.dataspaceconnector.repository.AgreementRepository;
import io.dataspaceconnector.repository.AppEndpointRepository;
import io.dataspaceconnector.repository.AppRepository;
import io.dataspaceconnector.repository.AppStoreRepository;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.BrokerRepository;
import io.dataspaceconnector.repository.CatalogRepository;
import io.dataspaceconnector.repository.ConfigurationRepository;
import io.dataspaceconnector.repository.ConnectorEndpointRepository;
import io.dataspaceconnector.repository.ContractRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.repository.DataSourceRepository;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.repository.OfferedResourcesRepository;
import io.dataspaceconnector.repository.RepresentationRepository;
import io.dataspaceconnector.repository.RequestedResourcesRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.repository.RuleRepository;
import io.dataspaceconnector.service.appstore.portainer.PortainerRequestService;
import io.dataspaceconnector.service.resource.ids.builder.IdsConfigModelBuilder;
import io.dataspaceconnector.service.resource.type.AgreementService;
import io.dataspaceconnector.service.resource.type.AppEndpointService;
import io.dataspaceconnector.service.resource.type.AppService;
import io.dataspaceconnector.service.resource.type.AppStoreService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.BrokerService;
import io.dataspaceconnector.service.resource.type.CatalogService;
import io.dataspaceconnector.service.resource.type.ConfigurationService;
import io.dataspaceconnector.service.resource.type.ConnectorEndpointService;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.DataSourceService;
import io.dataspaceconnector.service.resource.type.EndpointServiceProxy;
import io.dataspaceconnector.service.resource.type.OfferedResourceService;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import io.dataspaceconnector.service.resource.type.RequestedResourceService;
import io.dataspaceconnector.service.resource.type.RouteService;
import io.dataspaceconnector.service.resource.type.RuleService;
import io.dataspaceconnector.service.routing.RouteHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publish resource service to spring.
 */
@Configuration
public class ResourceConfig {

    /**
     * Create an agreement service bean.
     *
     * @param repo The agreement repo.
     * @return The agreement service.
     */
    @Bean("agreementService")
    public AgreementService createAgreementService(
            @Qualifier("agreementRepository") final AgreementRepository repo) {
        return new AgreementService(repo, new AgreementFactory());
    }

    /**
     * Create an artifact service bean.
     *
     * @param repository     The artifact repository.
     * @param dataRepository The data repository.
     * @param httpService    The http service.
     * @param authRepo       The auth repo.
     * @return The artifact service bean.
     */
    @Bean("artifactService")
    public ArtifactService createArtifactService(
            @Qualifier("artifactRepository") final ArtifactRepository repository,
            final DataRepository dataRepository,
            final HttpService httpService,
            final AuthenticationRepository authRepo) {
        return new ArtifactService(repository, new ArtifactFactory(),
                dataRepository, httpService, authRepo);
    }

    /**
     * Create an app service bean.
     *
     * @param repository The app repository.
     * @param appStoreSvc The appstore service.
     * @param dataRepo The data repository.
     * @param portainerSvc The portainer service.
     * @return The app service bean.
     */
    @Bean("appService")
    public AppService createAppService(
            final AppRepository repository,
            final AppStoreService appStoreSvc,
            final DataRepository dataRepo,
            final PortainerRequestService portainerSvc) {
        return new AppService(repository, new AppFactory(), appStoreSvc, dataRepo, portainerSvc);
    }

    /**
     * Create an appendpoint service bean.
     *
     * @param repository The appendpoint repository.
     * @param routeRepo The route repository.
     * @param routeHelper The route helper.
     * @return The appendpoint service bean.
     */
    @Bean("appEndpointService")
    public AppEndpointService createAppEndpointService(
            final AppEndpointRepository repository,
            final RouteRepository routeRepo,
            final RouteHelper routeHelper) {
        return new AppEndpointService(repository, new AppEndpointFactory(), routeRepo, routeHelper);
    }

    /**
     * Create an appstore service bean.
     *
     * @param repository The appstore repository.
     * @return The appstore service beam.
     */
    @Bean("appStoreService")
    public AppStoreService createAppStoreService(final AppStoreRepository repository) {
        return new AppStoreService(repository, new AppStoreFactory());
    }

    /**
     * Create a broker service bean.
     *
     * @param repo The broker repository.
     * @return The broker service bean.
     */
    @Bean("configurationBrokerService")
    public BrokerService createBrokerService(final BrokerRepository repo) {
        return new BrokerService(repo, new BrokerFactory());
    }

    /**
     * Create a catalog service bean.
     *
     * @param repo The catalog repository.
     * @return The catalog bean.
     */
    @Bean("catalogService")
    public CatalogService createCatalogService(final CatalogRepository repo) {
        return new CatalogService(repo, new CatalogFactory());
    }

    /**
     * Create a configuration service bean.
     *
     * @param repo            The configuration repository.
     * @param lookUp          The application context.
     * @param idsConfigBld    The IDS configuration builder.
     * @param connectorConfig The connector configuration.
     * @return The configuration service bean.
     */
    @Bean("configurationService")
    public ConfigurationService createConfigurationService(
            final ConfigurationRepository repo,
            final ServiceLookUp lookUp,
            final IdsConfigModelBuilder idsConfigBld,
            final ConnectorConfig connectorConfig) {
        return new ConfigurationService(repo, new ConfigurationFactory(new ProxyFactory(),
                new TruststoreFactory(),
                new KeystoreFactory(),
                connectorConfig),
                lookUp, idsConfigBld);
    }

    /**
     * Create a connectorEndpoint service bean.
     *
     * @param repo             The connectorEndpoint repository.
     * @param factory          The connectorEndpoint factory.
     * @param routeRepository  The route repository.
     * @param camelRouteHelper The camel route helper.
     * @return The connectorEndpoint service bean.
     */
    @Bean("connectorEndpointService")
    public ConnectorEndpointService createConnectorEndpointService(
            final ConnectorEndpointRepository repo,
            final ConnectorEndpointFactory factory,
            final RouteRepository routeRepository,
            final RouteHelper camelRouteHelper) {
        return new ConnectorEndpointService(repo, factory,
                routeRepository, camelRouteHelper);
    }

    /**
     * Creat a contract service bean.
     *
     * @param repo The contract repository.
     * @return The contract service bean.
     */
    @Bean("contractService")
    public ContractService createContractService(final ContractRepository repo) {
        return new ContractService(repo, new ContractFactory());
    }

    /**
     * Create a datasource service bean.
     *
     * @param repo The datasource repository.
     * @return The datasource service bean.
     */
    @Bean("dataSourceService")
    public DataSourceService createDataSourceService(final DataSourceRepository repo) {
        return new DataSourceService(repo, new DataSourceFactory());
    }

    /**
     * Create an offeredresource service bean.
     *
     * @param repo The offeredresource repository.
     * @return The offeredresource service bean.
     */
    @Bean("offeredResourceService")
    public OfferedResourceService createOfferedResourceService(
            @Qualifier("offeredResourcesRepository") final OfferedResourcesRepository repo) {
        return new OfferedResourceService(repo);
    }


    /**
     * Create a representation service bean.
     *
     * @param repo The representation repository.
     * @return The representation service bean.
     */
    @Bean("representationService")
    public RepresentationService createRepresentationService(final RepresentationRepository repo) {
        return new RepresentationService(repo, new RepresentationFactory());
    }

    /**
     * Create a requestedresource service bean.
     *
     * @param repo The requestedresource repository.
     * @return The requestedresource service bean.
     */
    @Bean("requestedResourceService")
    public RequestedResourceService createRequestedResourceService(
            @Qualifier("requestedResourcesRepository") final RequestedResourcesRepository repo) {
        return new RequestedResourceService(repo, new RequestedResourceFactory());
    }

    /**
     * Create a route service bean.
     *
     * @param repo                 The route repository.
     * @param endpointRepository   The endpoint repository.
     * @param endpointServiceProxy The endpoint service proxy.
     * @param routeHelper          The route helper.
     * @return The route service bean.
     */
    @Bean("routeService")
    public RouteService createResourceService(
            final RouteRepository repo,
            final EndpointRepository endpointRepository,
            final EndpointServiceProxy endpointServiceProxy,
            final RouteHelper routeHelper) {
        return new RouteService(repo, new RouteFactory(), endpointRepository,
                endpointServiceProxy, routeHelper);
    }

    /**
     * Create a rule service bean.
     *
     * @param repo The rule repository.
     * @return The rule service bean.
     */
    @Bean("ruleService")
    public RuleService createRuleService(final RuleRepository repo) {
        return new RuleService(repo, new ContractRuleFactory());
    }

    /**
     * Create a connectorEndpoint factory.
     *
     * @return The connectorEndpoint factory.
     */
    @Bean("connectorEndpointFactory")
    public ConnectorEndpointFactory createConnectorEndpointFactory() {
        return new ConnectorEndpointFactory();
    }
}
