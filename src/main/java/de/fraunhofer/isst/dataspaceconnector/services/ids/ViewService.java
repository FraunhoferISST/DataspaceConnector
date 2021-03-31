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
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.util.IDSUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        // Build children.
        final var resources = CompletableFuture.supplyAsync(
                () -> batchCreateResource(catalog.getOfferedResources()));

        try {
            final var uri = EndpointUtils.getSelfLink(catalog);
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
     * @return List of ids resources.
     */
    public List<Resource> batchCreateResource(final Collection<OfferedResource> resources) {
        return resources.parallelStream()
                .map(this::create)
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
        // Build children.
        final var contracts = CompletableFuture.supplyAsync(
                () -> batchCreateContract(resource.getContracts()));
        final var representations = CompletableFuture.supplyAsync(
                () -> batchCreateRepresentation(resource.getRepresentations()));

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

            final var uri = EndpointUtils.getSelfLink((OfferedResource) resource);
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
     * @return List of ids representations.
     */
    public List<Representation> batchCreateRepresentation(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Representation>
                    representations) {
        return representations.parallelStream()
                .map(this::create)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids representation from database representation and its children.
     *
     * @param representation The representation.
     * @return The ids representation.
     */
    public Representation create(final de.fraunhofer.isst.dataspaceconnector.model.Representation representation) {
        // Build children.
        final var artifacts = CompletableFuture.supplyAsync(
                () -> batchCreateArtifact(representation.getArtifacts()));

        try {
            // Prepare representation attributes.
            final var modified = IdsUtils.getGregorianOf(representation.getModificationDate());
            final var created = IdsUtils.getGregorianOf(representation.getCreationDate());
            final var language = IdsUtils.getLanguage(representation.getLanguage());
            final var mediaType = representation.getMediaType();
            final var standard = representation.getStandard();

            final var uri = EndpointUtils.getSelfLink(representation);
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
     * @return List of ids artifacts.
     */
    public List<Artifact> batchCreateArtifact(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Artifact> artifacts) {
        return artifacts.parallelStream()
                .map(this::create)
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
            // Prepare artifact attributes.
            final var created = IdsUtils.getGregorianOf(artifact.getCreationDate());
            final var title = artifact.getTitle();

            final var uri = EndpointUtils.getSelfLink(artifact);
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
     * @return List of ids contract offers.
     */
    public List<ContractOffer> batchCreateContract(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Contract> contracts) {
        return contracts.parallelStream()
                .map(this::create)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids contract from database contract and its children.
     *
     * @param contract The contract offer.
     * @return THe contract offer.
     */
    public ContractOffer create(final de.fraunhofer.isst.dataspaceconnector.model.Contract contract) {
        // Build children.
        final var rules = contract.getRules();
        final var permissions =
                CompletableFuture.supplyAsync(() -> batchCreatePermission(rules));
        final var prohibitions =
                CompletableFuture.supplyAsync(() -> batchCreateProhibition(rules));
        final var obligations =
                CompletableFuture.supplyAsync(() -> batchCreateObligation(rules));

        try {
            // Prepare contract attributes.
            final var contractStart = contract.getStart();
            final var contractEnd = contract.getEnd();
            final var consumer = contract.getConsumer();
            final var provider = contract.getProvider();

            final var uri = EndpointUtils.getSelfLink(contract);
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
     * @return List of ids obligations.
     */
    private List<Duty> batchCreateObligation(final List<ContractRule> rules) {
        return rules.parallelStream()
                .map(this::createObligation)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Create list of ids prohibitions.
     *
     * @param rules List of database rules.
     * @return List of ids prohibitions.
     */
    private List<Prohibition> batchCreateProhibition(final List<ContractRule> rules) {
        return rules.parallelStream()
                .map(this::createProhibition)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Create list of ids permissions.
     *
     * @param rules List of database rules.
     * @return List of ids permissions.
     */
    private List<Permission> batchCreatePermission(final List<ContractRule> rules) {
        return rules.parallelStream()
                .map(this::createPermission)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids obligation from database rule.
     *
     * @param rule The rule.
     * @return The ids obligation.
     */
    private Duty createObligation(final ContractRule rule) {
        try {
            final var idsRule = deserializationService.getRule(rule.getValue());
            if (idsRule instanceof Duty) {
                final var obligation = (Duty) idsRule;
                final var uri = EndpointUtils.getSelfLink(rule);
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
    private Prohibition createProhibition(final ContractRule rule) {
        try {
            final var idsRule = deserializationService.getRule(rule.getValue());
            if (idsRule instanceof Prohibition) {
                final var prohibition = (Prohibition) idsRule;
                final var uri = EndpointUtils.getSelfLink(rule);
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
    private Permission createPermission(final ContractRule rule) {
        try {
            final var idsRule = deserializationService.getRule(rule.getValue());
            if (idsRule instanceof Permission) {
                final var permission = (Permission) idsRule;
                final var uri = EndpointUtils.getSelfLink(rule);
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
}
