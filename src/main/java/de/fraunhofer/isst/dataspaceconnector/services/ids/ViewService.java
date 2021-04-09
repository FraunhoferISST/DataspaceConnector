package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.SelfLinkCreationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.util.IDSUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public final class ViewService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewService.class);

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Build ids catalog from database contract and its children.
     *
     * @param catalog The catalog.
     * @return The ids catalog.
     */
    public ResourceCatalog create(final Catalog catalog) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return create(catalog, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct ResourceCatalog: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids catalog from database contract and its children using the specified base URL for
     * creating the IDs, as scheme, host and port are missing when no request context is available.
     *
     * @param catalog The catalog.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The ids catalog.
     */
    private ResourceCatalog create(final Catalog catalog, final String baseUrl) {
        // Build children.
        final var resources = CompletableFuture.supplyAsync(
                () -> batchCreateResource(catalog.getOfferedResources(), baseUrl));

        try {

            final var uri = getAbsoluteSelfLink(catalog, baseUrl);

            final var idsCatalog = new ResourceCatalogBuilder(uri)
                    ._offeredResource_((ArrayList<? extends Resource>) resources.get())
                    .build();

            // Add additional attributes as property map.
            final var additional = catalog.getAdditional();
            for (final var entry : additional.entrySet()) {
                idsCatalog.setProperty(entry.getKey(), entry.getValue());
            }

            return idsCatalog;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build catalog. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Create list of ids resources.
     *
     * @param resources List of database resources.
     * @param baseUrl The application's base URL to use for the self links.
     * @return List of ids resources.
     */
    public List<Resource> batchCreateResource(final Collection<OfferedResource> resources,
                                              final String baseUrl) {
        return resources.parallelStream()
                .map(r -> this.create(r, baseUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids resource from database resource and its children. TODO Extend data model.
     *
     * @param resource The resource.
     * @return The ids resource.
     */
    public Resource create(final de.fraunhofer.isst.dataspaceconnector.model.Resource resource) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return create(resource, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct Resource: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids resource from database resource and its children using the specified base URL for
     * creating the IDs, as scheme, host and port are missing when no request context is available.
     *
     * TODO Extend data model.
     *
     * @param resource The resource.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The ids resource.
     */
    private Resource create(final de.fraunhofer.isst.dataspaceconnector.model.Resource resource,
                            final String baseUrl) {
        // Build children.
        final var contracts = CompletableFuture.supplyAsync(
                () -> batchCreateContract(resource.getContracts(), baseUrl));
        final var representations = CompletableFuture.supplyAsync(
                () -> batchCreateRepresentation(resource.getRepresentations(), baseUrl));

        try {
            // Prepare resource attributes.
            final var created = resource.getCreationDate();
            final var description = resource.getDescription();
            final var language = resource.getLanguage();
            final var idsLanguage = IdsUtils.getLanguage(resource.getLanguage());
            final var keywords = IdsUtils.getKeywordsAsTypedLiteral(resource.getKeywords(),
                    language);
            final var license = resource.getLicence();
            final var modified = resource.getModificationDate();
            final var publisher = resource.getPublisher();
            final var sovereign = resource.getSovereign();
            final var title = resource.getTitle();
            final var version = resource.getVersion();
            final var endpointDocs = resource.getEndpointDocumentation();

            final var uri = getAbsoluteSelfLink((OfferedResource) resource, baseUrl);

            final var endpoint = new ConnectorEndpointBuilder()
                    ._accessURL_(uri)
                    ._endpointDocumentation_(Util.asList(endpointDocs))
                    .build();

            final var idsResource = new ResourceBuilder(uri)
//                    ._accrualPeriodicity_()
//                    ._assetRefinement_()
//                    ._contentType_()
                    ._contractOffer_((ArrayList<? extends ContractOffer>) contracts.get())
                    ._created_(IdsUtils.getGregorianOf(created))
                    ._description_(Util.asList(new TypedLiteral(description, language)))
                    ._language_(Util.asList(idsLanguage))
                    ._keyword_((ArrayList<? extends TypedLiteral>) keywords)
                    ._modified_(IdsUtils.getGregorianOf(modified))
                    ._publisher_(publisher)
                    ._representation_((ArrayList<? extends Representation>) representations.get())
                    ._resourceEndpoint_(Util.asList(endpoint))
                    ._sovereign_(sovereign)
//                    ._spatialCoverage_()
//                    ._shapesGraph_()
                    ._standardLicense_(license)
//                    ._temporalCoverage_()
//                    ._temporalResolution_()
                    ._title_(Util.asList(new TypedLiteral(title, language)))
                    ._version_(String.valueOf(version))
                    .build();

            // Add additional attributes as property map.
            final var additional = resource.getAdditional();
            for (final var entry : additional.entrySet()) {
                idsResource.setProperty(entry.getKey(), entry.getValue());
            }

            return idsResource;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build resource. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    // NOTE: naming differently since eis.Representation and eis.Artifact produce same signature

    /**
     * Create list of ids representations.
     *
     * @param representations List of database representations.
     * @param baseUrl The application's base URL to use for the self links.
     * @return List of ids representations.
     */
    public List<Representation> batchCreateRepresentation(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Representation>
                    representations, final String baseUrl) {
        return representations.parallelStream()
                .map(r -> this.create(r, baseUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids representation from database representation and its children.
     *
     * @param representation The representation.
     * @return The ids representation.
     */
    public Representation create(final de.fraunhofer.isst.dataspaceconnector.model.Representation
                                         representation) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return create(representation, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct Representation: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids representation from database representation and its children using the specified
     * base URL for creating the IDs, as scheme, host and port are missing when no request context
     * is available.
     *
     * @param representation The representation.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The ids representation.
     */
    private Representation create(final de.fraunhofer.isst.dataspaceconnector.model.Representation
                                          representation, final String baseUrl) {
        // Build children.
        final var artifacts = CompletableFuture.supplyAsync(
                () -> batchCreateArtifact(representation.getArtifacts(), baseUrl));

        try {
            // Prepare representation attributes.
            final var modified = IdsUtils.getGregorianOf(representation
                    .getModificationDate());
            final var created = IdsUtils.getGregorianOf(representation
                    .getCreationDate());
            final var language = IdsUtils.getLanguage(representation.getLanguage());
            final var mediaType = representation.getMediaType();
            final var standard = representation.getStandard();

            final var uri = getAbsoluteSelfLink(representation, baseUrl);

            final var idsRepresentation = new RepresentationBuilder(uri)
                    ._created_(created)
                    ._instance_((ArrayList<? extends Artifact>) artifacts.get())
                    ._language_(language)
                    ._mediaType_(new IANAMediaTypeBuilder()._filenameExtension_(mediaType).build())
                    ._modified_(modified)
                    ._representationStandard_(URI.create(standard))
                    .build();

            // Add additional attributes as property map.
            final var additional = representation.getAdditional();
            for (final var entry : additional.entrySet()) {
                idsRepresentation.setProperty(entry.getKey(), entry.getValue());
            }

            return idsRepresentation;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build representation. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Create list of ids artifacts.
     *
     * @param artifacts List of database artifacts.
     * @param baseUrl The application's base URL to use for the self links.
     * @return List of ids artifacts.
     */
    public List<Artifact> batchCreateArtifact(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Artifact> artifacts,
            final String baseUrl) {
        return artifacts.parallelStream()
                .map(a -> this.create(a, baseUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids artifact from database artifact and its children.
     *
     * @param artifact The artifact.
     * @return The ids artifact.
     */
    public Artifact create(final de.fraunhofer.isst.dataspaceconnector.model.Artifact artifact) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return create(artifact, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct Artifact: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids artifact from database artifact and its children using the specified base URL for
     * creating the IDs, as scheme, host and port are missing when no request context is available.
     *
     * @param artifact The artifact.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The ids artifact.
     */
    private Artifact create(final de.fraunhofer.isst.dataspaceconnector.model.Artifact artifact,
                            final String baseUrl) {
        try {
            // Prepare artifact attributes.
            final var created = IdsUtils.getGregorianOf(artifact
                    .getCreationDate());
            final var title = artifact.getTitle();

            final var uri = getAbsoluteSelfLink(artifact, baseUrl);

            final var idsArtifact = new ArtifactBuilder(uri)
                    ._byteSize_(BigInteger.ONE) // TODO get the real file size (how?)
                    ._creationDate_(created)
                    ._fileName_(title)
                    .build();

            // Add additional attributes as property map.
            final var additional = artifact.getAdditional();
            for (final var entry : additional.entrySet()) {
                idsArtifact.setProperty(entry.getKey(), entry.getValue());
            }

            return idsArtifact;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build artifact. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Create list of ids contract offers.
     *
     * @param contracts List of database contract offers.
     * @param baseUrl The application's base URL to use for the self links.
     * @return List of ids contract offers.
     */
    public List<ContractOffer> batchCreateContract(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Contract> contracts,
            final String baseUrl) {
        return contracts.parallelStream()
                .map(c -> this.create(c, baseUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids contract from database contract and its children.
     *
     * @param contract The contract offer.
     * @return The contract offer.
     */
    public ContractOffer create(final de.fraunhofer.isst.dataspaceconnector.model.Contract
                                        contract) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return create(contract, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct ContractOffer: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids contract from database contract and its children using the specified base URL for
     * creating the IDs, as scheme, host and port are missing when no request context is available.
     *
     * @param contract The contract offer.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The contract offer.
     */
    private ContractOffer create(final de.fraunhofer.isst.dataspaceconnector.model.Contract
                                         contract, final String baseUrl) {
        // Build children.
        final var rules = contract.getRules();
        final var permissions =
                CompletableFuture.supplyAsync(() -> batchCreatePermission(rules, baseUrl));
        final var prohibitions =
                CompletableFuture.supplyAsync(() -> batchCreateProhibition(rules, baseUrl));
        final var obligations =
                CompletableFuture.supplyAsync(() -> batchCreateObligation(rules, baseUrl));

        try {
            // Prepare contract attributes.
            final var contractStart = contract.getStart();
            final var contractEnd = contract.getEnd();
            final var consumer = contract.getConsumer();
            final var provider = contract.getProvider();

            final var uri = getAbsoluteSelfLink(contract, baseUrl);

            final var idsContract = new ContractOfferBuilder(uri)
                    ._permission_((ArrayList<? extends Permission>) permissions.get())
                    ._prohibition_((ArrayList<? extends Prohibition>) prohibitions.get())
                    ._obligation_((ArrayList<? extends Duty>) obligations.get())
                    ._contractStart_(IdsUtils.getGregorianOf(contractStart))
                    ._contractDate_(IDSUtils.getGregorianNow())
                    ._consumer_(consumer)
                    ._provider_(provider)
                    ._contractEnd_(IdsUtils.getGregorianOf(contractEnd))
                    .build();

            // Add additional attributes as property map.
            final var additional = contract.getAdditional();
            for (final var entry : additional.entrySet()) {
                idsContract.setProperty(entry.getKey(), entry.getValue());
            }

            return idsContract;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build contract. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Create list of ids obligations.
     *
     * @param rules List of database rules.
     * @param baseUrl The application's base URL to use for the self links.
     * @return List of ids obligations.
     */
    private List<Duty> batchCreateObligation(final List<ContractRule> rules, final String baseUrl) {
        return rules.parallelStream()
                .map(r -> this.createObligation(r, baseUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Create list of ids prohibitions.
     *
     * @param rules List of database rules.
     * @param baseUrl The application's base URL to use for the self links.
     * @return List of ids prohibitions.
     */
    private List<Prohibition> batchCreateProhibition(final List<ContractRule> rules,
                                                     final String baseUrl) {
        return rules.parallelStream()
                .map(r -> this.createProhibition(r, baseUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Create list of ids permissions.
     *
     * @param rules List of database rules.
     * @param baseUrl The application's base URL to use for the self links.
     * @return List of ids permissions.
     */
    private List<Permission> batchCreatePermission(final List<ContractRule> rules,
                                                   final String baseUrl) {
        return rules.parallelStream()
                .map(r -> this.createPermission(r, baseUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids obligation from database rule.
     *
     * @param rule The rule.
     * @return The ids obligation.
     */
    public Duty createObligation(final ContractRule rule) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return createObligation(rule, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct Duty: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids obligation from database rule using the specified base URL for creating the IDs,
     * as scheme, host and port are missing when no request context is available.
     *
     * @param rule The rule.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The ids obligation.
     */
    private Duty createObligation(final ContractRule rule, final String baseUrl) {
        try {
            final var idsRule = deserializationService.getRule(rule.getValue());
            if (idsRule instanceof Duty) {
                final var obligation = (Duty) idsRule;

                final var uri = getAbsoluteSelfLink(rule, baseUrl);

                return new DutyBuilder(uri)
                        ._action_(obligation.getAction())
                        ._assignee_(obligation.getAssignee())
                        ._assigner_(obligation.getAssigner())
                        ._constraint_(obligation.getConstraint())
                        ._description_(obligation.getDescription())
                        ._target_(obligation.getTarget())
                        ._title_(obligation.getTitle())
                        .build();
            }
            return null;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build rule. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Build ids prohibition from database rule.
     *
     * @param rule The rule.
     * @return The ids prohibition.
     */
    public Prohibition createProhibition(final ContractRule rule) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return createProhibition(rule, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct Prohibition: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids prohibition from database rule using the specified base URL for creating the IDs,
     * as scheme, host and port are missing when no request context is available.
     *
     * @param rule The rule.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The ids prohibition.
     */
    private Prohibition createProhibition(final ContractRule rule, final String baseUrl) {
        try {
            final var idsRule = deserializationService.getRule(rule.getValue());
            if (idsRule instanceof Prohibition) {
                final var prohibition = (Prohibition) idsRule;

                final var uri = getAbsoluteSelfLink(rule, baseUrl);

                return new ProhibitionBuilder(uri)
                        ._action_(prohibition.getAction())
                        ._assignee_(prohibition.getAssignee())
                        ._assigner_(prohibition.getAssigner())
                        ._constraint_(prohibition.getConstraint())
                        ._description_(prohibition.getDescription())
                        ._target_(prohibition.getTarget())
                        ._title_(prohibition.getTitle())
                        .build();
            }
            return null;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build rule. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Build ids permission from database rule.
     *
     * @param rule The rule.
     * @return The ids permission.
     */
    public Permission createPermission(final ContractRule rule) {
        try {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            return createPermission(rule, baseUrl);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Failed to construct Permission: no request context present to "
                    + "construct self links.");
            throw new SelfLinkCreationException(ErrorMessages.NO_REQUEST_CONTEXT, exception);
        }
    }

    /**
     * Build ids permission from database rule using the specified base URL for creating the IDs,
     * as scheme, host and port are missing when no request context is available.
     *
     * @param rule The rule.
     * @param baseUrl The application's base URL to use for the self links.
     * @return The ids permission.
     */
    private Permission createPermission(final ContractRule rule, final String baseUrl) {
        try {
            final var idsRule = deserializationService.getRule(rule.getValue());
            if (idsRule instanceof Permission) {
                final var permission = (Permission) idsRule;

                final var uri = getAbsoluteSelfLink(rule, baseUrl);

                return new PermissionBuilder(uri)
                        ._action_(permission.getAction())
                        ._assignee_(permission.getAssignee())
                        ._assigner_(permission.getAssigner())
                        ._constraint_(permission.getConstraint())
                        ._description_(permission.getDescription())
                        ._preDuty_(permission.getPreDuty())
                        ._postDuty_(permission.getPostDuty())
                        ._target_(permission.getTarget())
                        ._title_(permission.getTitle())
                        .build();
            }
            return null;
        } catch (Exception exception) {
            LOGGER.warn("Failed to build rule. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Returns the self link to an entity as an absolute URI. As the self link returned by Spring
     * can be either absolute or relative, this method adds the application's base URL to the link
     * if it is relative.
     *
     * @param entity The entity.
     * @param baseUrl The application's base URL to use for the self link.
     * @return The self link to the entity as an absolute URI.
     */
    private URI getAbsoluteSelfLink(final AbstractEntity entity, final String baseUrl) {
        URI uri;
        if (entity instanceof Catalog) {
            uri = EndpointUtils.getSelfLink((Catalog) entity);
        } else if (entity instanceof OfferedResource) {
            uri = EndpointUtils.getSelfLink((OfferedResource) entity);
        } else if (entity instanceof RequestedResource) {
            uri = EndpointUtils.getSelfLink((RequestedResource) entity);
        } else if (entity instanceof de.fraunhofer.isst.dataspaceconnector.model.Representation) {
            uri = EndpointUtils.getSelfLink(
                    (de.fraunhofer.isst.dataspaceconnector.model.Representation) entity);
        } else if (entity instanceof de.fraunhofer.isst.dataspaceconnector.model.Artifact) {
            uri = EndpointUtils.getSelfLink(
                    (de.fraunhofer.isst.dataspaceconnector.model.Artifact) entity);
        } else if (entity instanceof Contract) {
            uri = EndpointUtils.getSelfLink((Contract) entity);
        } else if (entity instanceof ContractRule) {
            uri = EndpointUtils.getSelfLink((ContractRule) entity);
        } else if (entity instanceof Agreement) {
            uri = EndpointUtils.getSelfLink((Agreement) entity);
        } else {
            throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
        }

        if (uri.toString().startsWith("/")) {
            uri = URI.create(baseUrl + uri);
        }

        return uri;
    }
}
