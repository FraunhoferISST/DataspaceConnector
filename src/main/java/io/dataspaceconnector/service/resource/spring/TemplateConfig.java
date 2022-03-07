/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import io.dataspaceconnector.service.resource.relation.AppEndpointLinker;
import io.dataspaceconnector.service.resource.relation.CatalogOfferedResourceLinker;
import io.dataspaceconnector.service.resource.relation.CatalogRequestedResourceLinker;
import io.dataspaceconnector.service.resource.relation.ContractRuleLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceContractLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceContractLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.templatebuilder.AppEndpointTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.AppTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.ArtifactTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.CatalogTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.ContractRuleTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.ContractTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.OfferedResourceTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.RepresentationTemplateBuilder;
import io.dataspaceconnector.service.resource.templatebuilder.RequestedResourceTemplateBuilder;
import io.dataspaceconnector.service.resource.type.AppEndpointService;
import io.dataspaceconnector.service.resource.type.AppService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.CatalogService;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.OfferedResourceService;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import io.dataspaceconnector.service.resource.type.RequestedResourceService;
import io.dataspaceconnector.service.resource.type.RuleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publishes templateBuilders to spring.
 */
@Configuration
public class TemplateConfig {

    /**
     * Creates a catalog template builder bean.
     *
     * @param catalogService   The catalog service.
     * @param offeredLinker    The offered resource linker.
     * @param requestedLinker  The requested resource linker.
     * @param offeredBuilder   The offered resource builder.
     * @param requestedBuilder The requested resource builder.
     * @return The catalog template builder bean.
     */
    @Bean("catalogTemplateBuilder")
    public CatalogTemplateBuilder createCatalogTemplateBuilder(
            final CatalogService catalogService,
            final CatalogOfferedResourceLinker offeredLinker,
            final CatalogRequestedResourceLinker requestedLinker,
            final OfferedResourceTemplateBuilder offeredBuilder,
            final RequestedResourceTemplateBuilder requestedBuilder) {
        return new CatalogTemplateBuilder(catalogService, offeredLinker, requestedLinker,
                offeredBuilder, requestedBuilder);
    }

    /**
     * Creates an offered resource template builder bean.
     *
     * @param offeredResourceService        The offered resource service.
     * @param resourceRepresentationLinker  The resource representation linker.
     * @param resourceContractLinker        The resource contract linker.
     * @param representationTemplateBuilder The representation template builder.
     * @param contractTemplateBuilder       The contract template builder.
     * @return The offered resource template builder bean.
     */
    @Bean("offeredResourceTemplateBuilder")
    public OfferedResourceTemplateBuilder createOfferedResourceTemplateBuilder(
            final OfferedResourceService offeredResourceService,
            final OfferedResourceRepresentationLinker resourceRepresentationLinker,
            final OfferedResourceContractLinker resourceContractLinker,
            final RepresentationTemplateBuilder representationTemplateBuilder,
            final ContractTemplateBuilder contractTemplateBuilder) {
        return new OfferedResourceTemplateBuilder(offeredResourceService,
                resourceRepresentationLinker,
                resourceContractLinker,
                representationTemplateBuilder,
                contractTemplateBuilder);
    }

    /**
     * Creates a requested resource template builder bean.
     *
     * @param requestedResourceService      The requested resource service.
     * @param resourceRepresentationLinker  The resource representation linker.
     * @param resourceContractLinker        The resource contract linker.
     * @param representationTemplateBuilder The representation template builder.
     * @param contractTemplateBuilder       The contract template builder.
     * @return The requested resource template builder bean.
     */
    @Bean("requestedResourceTemplateBuilder")
    public RequestedResourceTemplateBuilder createRequestedResourceTemplateBuilder(
            final RequestedResourceService requestedResourceService,
            final RequestedResourceRepresentationLinker resourceRepresentationLinker,
            final RequestedResourceContractLinker resourceContractLinker,
            final RepresentationTemplateBuilder representationTemplateBuilder,
            final ContractTemplateBuilder contractTemplateBuilder) {
        return new RequestedResourceTemplateBuilder(requestedResourceService,
                resourceRepresentationLinker,
                resourceContractLinker,
                representationTemplateBuilder,
                contractTemplateBuilder);
    }

    /**
     * Creates a representation template builder bean.
     *
     * @param representationService        The representation service.
     * @param representationArtifactLinker The representation artifact linker.
     * @param artifactService              The artifact service.
     * @return The representation template builder bean.
     */
    @Bean("representationTemplateBuilder")
    public RepresentationTemplateBuilder createRepresentationTemplateBuilder(
            final RepresentationService representationService,
            final RepresentationArtifactLinker representationArtifactLinker,
            final ArtifactService artifactService) {
        return new RepresentationTemplateBuilder(representationService,
                representationArtifactLinker,
                new ArtifactTemplateBuilder(artifactService));
    }

    /**
     * Creates an app template builder bean.
     *
     * @param appService The app template service.
     * @param appEndpointLinker The app endpoint linker.
     * @param appEndpointService The app endpoint service.
     * @return The app template builder bean.
     */
    @Bean("appTemplateBuilder")
    public AppTemplateBuilder createAppTemplateBuilder(
            final AppService appService,
            final AppEndpointLinker appEndpointLinker,
            final AppEndpointService appEndpointService) {
        return new AppTemplateBuilder(appService, appEndpointLinker,
                                      new AppEndpointTemplateBuilder(appEndpointService));
    }

    /**
     * Creates a contract template builder bean.
     *
     * @param contractService    The contract service.
     * @param contractRuleLinker The contract rule linker.
     * @param ruleService        The rule service.
     * @return The contract template builder bean.
     */
    @Bean("contractTemplateBuilder")
    public ContractTemplateBuilder createContractTemplateBuilder(
            final ContractService contractService,
            final ContractRuleLinker contractRuleLinker,
            final RuleService ruleService) {
        return new ContractTemplateBuilder(contractService, contractRuleLinker,
                new ContractRuleTemplateBuilder(ruleService));
    }
}
