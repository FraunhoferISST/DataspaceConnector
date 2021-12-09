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
package io.dataspaceconnector.service.resource.templatebuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.service.resource.base.RemoteResolver;
import io.dataspaceconnector.service.resource.relation.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.relation.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.type.ResourceService;

/**
 * Template builder for requested resources.
 */
public class RequestedResourceTemplateBuilder
        extends AbstractResourceTemplateBuilder<RequestedResource, RequestedResourceDesc> {
    /**
     * Default constructor.
     *
     * @param resourceService               The resource service.
     * @param resourceRepresentationLinker  The resource-representation service.
     * @param resourceContractLinker        The resource-contract linker.
     * @param representationTemplateBuilder The representation template builder.
     * @param contractTemplateBuilder       The contract template builder.
     */
    public RequestedResourceTemplateBuilder(
            final ResourceService<RequestedResource, RequestedResourceDesc> resourceService,
            final AbstractResourceRepresentationLinker<RequestedResource>
                    resourceRepresentationLinker,
            final AbstractResourceContractLinker<RequestedResource> resourceContractLinker,
            final RepresentationTemplateBuilder representationTemplateBuilder,
            final ContractTemplateBuilder contractTemplateBuilder) {
        super(resourceService, resourceRepresentationLinker, resourceContractLinker,
                representationTemplateBuilder, contractTemplateBuilder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressFBWarnings("BC_IMPOSSIBLE_CAST")
    protected RequestedResource buildResource(
            final ResourceTemplate<RequestedResourceDesc> template) {
        final var resourceService = getResourceService();

        if (resourceService instanceof RemoteResolver) {
            final var resourceId = ((RemoteResolver) resourceService)
                    .identifyByRemoteId(template.getDesc().getRemoteId());
            if (resourceId.isPresent()) {
                return resourceService.update(resourceId.get(), template.getDesc());
            }
        }

        return resourceService.create(template.getDesc());
    }
}
