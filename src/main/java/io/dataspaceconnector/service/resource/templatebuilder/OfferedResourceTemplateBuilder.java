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
package io.dataspaceconnector.service.resource.templatebuilder;

import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.service.resource.relation.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.relation.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.type.ResourceService;

/**
 * Template builder for offered resources.
 */
public class OfferedResourceTemplateBuilder
        extends AbstractResourceTemplateBuilder<OfferedResource, OfferedResourceDesc> {
    /**
     * Default constructor.
     *
     * @param resourceService               The resource service.
     * @param resourceRepresentationLinker  The resource-representation service.
     * @param resourceContractLinker        The resource-contract linker.
     * @param representationTemplateBuilder The representation template builder.
     * @param contractTemplateBuilder       The contract template builder.
     */
    public OfferedResourceTemplateBuilder(
            final ResourceService<OfferedResource, OfferedResourceDesc> resourceService,
            final AbstractResourceRepresentationLinker<OfferedResource>
                    resourceRepresentationLinker,
            final AbstractResourceContractLinker<OfferedResource> resourceContractLinker,
            final RepresentationTemplateBuilder representationTemplateBuilder,
            final ContractTemplateBuilder contractTemplateBuilder) {
        super(resourceService, resourceRepresentationLinker, resourceContractLinker,
                representationTemplateBuilder, contractTemplateBuilder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OfferedResource buildResource(final ResourceTemplate<OfferedResourceDesc> template) {
        return getResourceService().create(template.getDesc());
    }
}
