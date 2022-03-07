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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.net.EndpointUtils;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.Resource;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import io.dataspaceconnector.service.resource.type.ResourceService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Converts dsc resource to ids resource.
 *
 * @param <T> The resource type.
 */
@Component
public final class IdsResourceBuilder<T extends Resource> extends AbstractIdsBuilder<T,
        de.fraunhofer.iais.eis.Resource> {

    /**
     * The builder for ids representation.
     */
    private final @NonNull IdsRepresentationBuilder repBuilder;

    /**
     * The builder for ids contract offer.
     */
    private final @NonNull IdsContractBuilder contractBuilder;

    /**
     * The service for resource handling.
     */
    private final @NonNull ResourceService<T, OfferedResourceDesc> resourceSvc;

    /**
     * Constructs an IdsResourceBuilder.
     *
     * @param selfLinkHelper the self link helper.
     * @param idsRepresentationBuilder the representation builder.
     * @param idsContractBuilder the contract builder.
     * @param resourceService the resource service.
     */
    @Autowired
    public IdsResourceBuilder(final SelfLinkHelper selfLinkHelper,
                             final IdsRepresentationBuilder idsRepresentationBuilder,
                             final IdsContractBuilder idsContractBuilder,
                             final ResourceService<T, OfferedResourceDesc> resourceService) {
        super(selfLinkHelper);
        this.repBuilder = idsRepresentationBuilder;
        this.contractBuilder = idsContractBuilder;
        this.resourceSvc = resourceService;
    }

    @Override
    protected de.fraunhofer.iais.eis.Resource createInternal(final Resource resource,
                                                             final int currentDepth,
                                                             final int maxDepth)
            throws ConstraintViolationException {
        // Build children.
        final var representations =
                create(repBuilder, resource.getRepresentations(), currentDepth, maxDepth);
        final var contracts =
                create(contractBuilder, resource.getContracts(), currentDepth, maxDepth);

        // Prepare resource attributes.
        final var selfLink = getAbsoluteSelfLink(resource);
        final var created = ToIdsObjectMapper.getGregorianOf(resource.getCreationDate());
        final var modified = ToIdsObjectMapper.getGregorianOf(resource.getModificationDate());
        final var description = resource.getDescription();
        final var idsLanguage = ToIdsObjectMapper.getLanguage(resource.getLanguage());
        final var languageCode = getLanguageCode(idsLanguage);
        final var keywords = ToIdsObjectMapper.getKeywordsAsTypedLiteral(resource.getKeywords(),
                languageCode);
        final var license = resource.getLicense();
        final var paymentModality = resource.getPaymentModality() == null
                ? null : ToIdsObjectMapper.getPaymentModality(resource.getPaymentModality());
        final var publisher = resource.getPublisher();
        final var sovereign = resource.getSovereign();
        final var title = resource.getTitle();
        final var version = resource.getVersion();
        final var endpointDocs = resource.getEndpointDocumentation();

        final var endpoint = new ConnectorEndpointBuilder()
                ._accessURL_(selfLink)
                ._endpointDocumentation_(Util.asList(endpointDocs))
                .build();

        // Get sample resources as list.
        var samples = new ArrayList<de.fraunhofer.iais.eis.Resource>();
        if (currentDepth <= maxDepth && maxDepth >= 0) {
            samples.addAll(resource.getSamples()
                    .stream()
                    .map(x -> this.create(resourceSvc.get(EndpointUtils.getUUIDFromPath(x)), -1))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        // Build resource only if at least one representation and one contract is present.
        if (representations.isEmpty() || contracts.isEmpty() || representations.get().isEmpty()
                || contracts.get().isEmpty()) {
            return null;
        }

        final var builder = new ResourceBuilder(selfLink)
                ._created_(created)
                ._description_(Util.asList(new TypedLiteral(description, languageCode)))
                ._language_(Util.asList(idsLanguage))
                ._keyword_(keywords)
                ._modified_(modified)
                ._paymentModality_(paymentModality)
                ._publisher_(publisher)
                ._resourceEndpoint_(Util.asList(endpoint))
                ._sovereign_(sovereign)
                ._standardLicense_(license)
                ._title_(Util.asList(new TypedLiteral(title, languageCode)))
                ._version_(String.valueOf(version));

        if (!samples.isEmpty()) {
            builder._sample_(samples);
        }

        representations.ifPresent(builder::_representation_);
        contracts.ifPresent(builder::_contractOffer_);

        return builder.build();
    }

    /**
     * Returns the language code for a language, so e.g. "EN" for the language with ID
     * "https://w3id.org/idsa/code/EN".
     *
     * @param language the language object.
     * @return the corresponding language code.
     */
    private String getLanguageCode(final Language language) {
        final var languageId = language.getId().toString();
        final var idParts = languageId.split("/");
        return idParts[idParts.length - 1];
    }
}
