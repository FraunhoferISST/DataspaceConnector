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
import io.dataspaceconnector.model.agreement.AgreementFactory;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.broker.BrokerFactory;
import io.dataspaceconnector.model.catalog.CatalogFactory;
import io.dataspaceconnector.model.configuration.ConfigurationFactory;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.model.datasource.DataSourceFactory;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointFactory;
import io.dataspaceconnector.model.representation.RepresentationFactory;
import io.dataspaceconnector.model.resource.OfferedResourceFactory;
import io.dataspaceconnector.model.resource.RequestedResourceFactory;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import io.dataspaceconnector.repository.AgreementRepository;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.BrokerRepository;
import io.dataspaceconnector.repository.CatalogRepository;
import io.dataspaceconnector.repository.ConfigurationRepository;
import io.dataspaceconnector.repository.ConnectorEndpointRepository;
import io.dataspaceconnector.repository.ContractRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.repository.DataSourceRepository;
import io.dataspaceconnector.repository.OfferedResourcesRepository;
import io.dataspaceconnector.repository.RepresentationRepository;
import io.dataspaceconnector.repository.RequestedResourcesRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.repository.RuleRepository;
import io.dataspaceconnector.service.resource.ids.builder.IdsConfigModelBuilder;
import io.dataspaceconnector.service.resource.type.AgreementService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.BrokerService;
import io.dataspaceconnector.service.resource.type.CatalogService;
import io.dataspaceconnector.service.resource.type.ConfigurationService;
import io.dataspaceconnector.service.resource.type.ConnectorEndpointService;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.DataSourceService;
import io.dataspaceconnector.service.resource.type.OfferedResourceService;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import io.dataspaceconnector.service.resource.type.RequestedResourceService;
import io.dataspaceconnector.service.resource.type.RuleService;
import io.dataspaceconnector.service.routing.RouteHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfig {
    @Bean("agreementService")
    public AgreementService getAgreementService(
            @Qualifier("agreementRepository") final AgreementRepository repo) {
        return new AgreementService(repo, new AgreementFactory());
    }

    @Bean("artifactService")
    public ArtifactService getArtifactService(
            final ArtifactRepository repository,
            final ArtifactFactory factory,
            final DataRepository dataRepository,
            final HttpService httpService,
            final AuthenticationRepository authRepo) {
        return new ArtifactService(repository, factory, dataRepository, httpService, authRepo);
    }

    @Bean("configurationBrokerService")
    public BrokerService getBrokerService(final BrokerRepository repo) {
        return new BrokerService(repo, new BrokerFactory());
    }

    @Bean("catalogService")
    public CatalogService getCatalogService(final CatalogRepository repo) {
        return new CatalogService(repo, new CatalogFactory());
    }

    @Bean("configurationService")
    public ConfigurationService getConfigurationService(final ConfigurationRepository repo,
                                                        final ConfigurationFactory factory,
                                                        final ApplicationContext context,
                                                        final IdsConfigModelBuilder configBuilder) {
        return new ConfigurationService(repo, factory, context, configBuilder);
    }

    @Bean("connectorEndpointService")
    public ConnectorEndpointService getConnectorEndpointService(
            final ConnectorEndpointRepository repo,
            final ConnectorEndpointFactory factory,
            final RouteRepository routeRepository,
            final RouteHelper camelRouteHelper) {
        return new ConnectorEndpointService(repo, factory, routeRepository, camelRouteHelper);
    }

    @Bean("contractService")
    public ContractService getContractService(final ContractRepository repo) {
        return new ContractService(repo, new ContractFactory());
    }

    @Bean("dataSourceService")
    public DataSourceService getDataSourceService(final DataSourceRepository repo) {
        return new DataSourceService(repo, new DataSourceFactory());
    }

    @Bean("offeredResourceService")
    public OfferedResourceService getOfferedResourceService(
            @Qualifier("offeredResourcesRepository") final OfferedResourcesRepository repo) {
        return new OfferedResourceService(repo, new OfferedResourceFactory());
    }

    @Bean("representationService")
    public RepresentationService getRepresentationService(final RepresentationRepository repo) {
        return new RepresentationService(repo, new RepresentationFactory());
    }

    @Bean("requestedResourceService")
    public RequestedResourceService getRequestedResourceService(
            @Qualifier("requestedResourcesRepository") final RequestedResourcesRepository repo) {
        return new RequestedResourceService(repo, new RequestedResourceFactory());
    }

    @Bean("ruleService")
    public RuleService getRuleService(final RuleRepository repo) {
        return new RuleService(repo, new ContractRuleFactory());
    }
}
