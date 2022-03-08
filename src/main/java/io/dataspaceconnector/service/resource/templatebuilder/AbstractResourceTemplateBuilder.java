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

import java.util.stream.Collectors;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.resource.Resource;
import io.dataspaceconnector.model.resource.ResourceDesc;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.service.resource.relation.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.relation.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.type.ResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds and links entities from templates.
 *
 * @param <T> The resource type.
 * @param <D> The resource description type.
 */
@Transactional
@RequiredArgsConstructor
public abstract class AbstractResourceTemplateBuilder<T extends Resource, D extends ResourceDesc> {

    /**
     * The service for resources.
     */
    private final @NonNull ResourceService<T, D> resourceService;

    /**
     * The linker for resource-representation relations.
     */
    private final @NonNull AbstractResourceRepresentationLinker<T> resourceRepresentationLinker;

    /**
     * The linker for resource-contract relations.
     */
    private final @NonNull AbstractResourceContractLinker<T> resourceContractLinker;

    /**
     * Builder for representation templates.
     */
    private final @NonNull RepresentationTemplateBuilder representationTemplateBuilder;

    /**
     * Builder for contract templates.
     */
    private final @NonNull ContractTemplateBuilder contractTemplateBuilder;

    /**
     * Build a resource and dependencies from a template.
     *
     * @param template The resource template.
     * @return The new resource.
     * @throws IllegalArgumentException if the passed template is null.
     */
    public T build(final ResourceTemplate<D> template) {
        Utils.requireNonNull(template, ErrorMessage.ENTITY_NULL);

        final var representationIds =
                Utils.toStream(template.getRepresentations()).map(x ->
                                            representationTemplateBuilder.build(x).getId())
                        .collect(Collectors.toSet());
        final var contractIds = Utils.toStream(template.getContracts()).map(x ->
                                            contractTemplateBuilder.build(x).getId())
                .collect(Collectors.toSet());
        final var resource = buildResource(template);

        resourceRepresentationLinker.add(resource.getId(), representationIds);
        resourceContractLinker.add(resource.getId(), contractIds);

        return resource;
    }

    /**
     * Creates a resource from a resource template.
     *
     * @param template the template.
     * @return the resource.
     */
    protected abstract T buildResource(ResourceTemplate<D> template);

    /**
     * Return the resource service for subclasses.
     *
     * @return The resource service.
     */
    @NonNull
    protected ResourceService<T, D> getResourceService() {
        return resourceService;
    }
}
