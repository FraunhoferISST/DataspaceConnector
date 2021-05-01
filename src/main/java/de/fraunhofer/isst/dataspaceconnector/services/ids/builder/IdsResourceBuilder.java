package de.fraunhofer.isst.dataspaceconnector.services.ids.builder;

import java.net.URI;
import java.util.ArrayList;

import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converts DSC resource to ids resource.
 *
 * @param <T> The resource type.
 */
@Component
@RequiredArgsConstructor
public final class IdsResourceBuilder<T extends Resource>
        extends AbstractIdsBuilder<T, de.fraunhofer.iais.eis.Resource> {

    /**
     * The builder for ids representation.
     */
    private final @NonNull IdsRepresentationBuilder repBuilder;

    /**
     * The builder for ids contract offer.
     */
    private final @NonNull IdsContractBuilder contractBuilder;

    @Override
    protected de.fraunhofer.iais.eis.Resource createInternal(final Resource resource,
                                                             final URI baseUri,
                                                             final int currentDepth,
                                                             final int maxDepth)
            throws ConstraintViolationException {
        // Build children.
        final var representations =
                create(repBuilder, resource.getRepresentations(), baseUri, currentDepth,
                        maxDepth);
        final var contracts =
                create(contractBuilder, resource.getContracts(), baseUri, currentDepth, maxDepth);

        // Prepare resource attributes.
        final var selfLink = getAbsoluteSelfLink(resource, baseUri);
        final var created = IdsUtils.getGregorianOf(resource.getCreationDate());
        final var modified = IdsUtils.getGregorianOf(resource.getModificationDate());
        final var description = resource.getDescription();
        final var language = resource.getLanguage();
        final var idsLanguage = IdsUtils.getLanguage(resource.getLanguage());
        final var keywords = IdsUtils.getKeywordsAsTypedLiteral(resource.getKeywords(),
                language);
        final var license = resource.getLicence();
        final var publisher = resource.getPublisher();
        final var sovereign = resource.getSovereign();
        final var title = resource.getTitle();
        final var version = resource.getVersion();
        final var endpointDocs = resource.getEndpointDocumentation();

        final var endpoint = new ConnectorEndpointBuilder()
                ._accessURL_(selfLink)
                ._endpointDocumentation_(Util.asList(endpointDocs))
                .build();

        final var builder = new ResourceBuilder(selfLink)
//                ._accrualPeriodicity_()
//                ._assetRefinement_()
//                ._contentType_()
                ._created_(created)
                ._description_(Util.asList(new TypedLiteral(description, language)))
                ._language_(Util.asList(idsLanguage))
                ._keyword_((ArrayList<? extends TypedLiteral>) keywords)
                ._modified_(modified)
                ._publisher_(publisher)
                ._resourceEndpoint_(Util.asList(endpoint))
                ._sovereign_(sovereign)
//                ._spatialCoverage_()
//                ._shapesGraph_()
                ._standardLicense_(license)
//                ._temporalCoverage_()
//                ._temporalResolution_()
                ._title_(Util.asList(new TypedLiteral(title, language)))
                ._version_(String.valueOf(version));

        representations.ifPresent(builder::_representation_);
        contracts.ifPresent(builder::_contractOffer_);

        return builder.build();
    }
}
