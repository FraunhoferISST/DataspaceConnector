package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.view.AgreementViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.ArtifactViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.CatalogViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.ContractRuleViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.ContractViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.OfferedResourceViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.RepresentationViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.RequestedResourceViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.view.SelfLinking;

import java.net.URI;

/**
 * This is a helper class for retrieving self-links of a database entity.
 */
public final class SelfLinkHelper {

    /**
     * View assembler for catalogs.
     */
    static final CatalogViewAssembler CATALOG_ASSEMBLER = new CatalogViewAssembler();

    /**
     * View assembler for offered resources.
     */
    static final OfferedResourceViewAssembler OFFERED_RESOURCE_ASSEMBLER
            = new OfferedResourceViewAssembler();

    /**
     * View assembler for requested resources.
     */
    static final RequestedResourceViewAssembler REQUESTED_RESOURCE_ASSEMBLER
            = new RequestedResourceViewAssembler();

    /**
     * View assembler for representations.
     */
    static final RepresentationViewAssembler REPRESENTATION_ASSEMBLER
            = new RepresentationViewAssembler();

    /**
     * View assembler for artifacts.
     */
    static final ArtifactViewAssembler ARTIFACT_ASSEMBLER = new ArtifactViewAssembler();

    /**
     * View assembler for contracts.
     */
    static final ContractViewAssembler CONTRACT_ASSEMBLER = new ContractViewAssembler();

    /**
     * View assembler for contract rules.
     */
    static final ContractRuleViewAssembler RULE_ASSEMBLER = new ContractRuleViewAssembler();

    /**
     * View assembler for contract agreements.
     */
    static final AgreementViewAssembler AGREEMENT_ASSEMBLER = new AgreementViewAssembler();

    /**
     * Default constructor.
     */
    private SelfLinkHelper() {
        // not used
    }

    /**
     * This function is a helper function for hiding the problem that the self-link is always
     * received through the concrete assembler.
     *
     * @param entity The entity.
     * @param <T>    Generic type of database entity.
     * @return The abstract entity.
     */
    public static <T extends AbstractEntity> URI getSelfLink(final T entity) {
        if (entity instanceof Catalog) {
            return getSelfLink((Catalog) entity);
        } else if (entity instanceof OfferedResource) {
            return getSelfLink((OfferedResource) entity);
        } else if (entity instanceof RequestedResource) {
            return getSelfLink((RequestedResource) entity);
        } else if (entity instanceof Representation) {
            return getSelfLink((Representation) entity);
        } else if (entity instanceof Artifact) {
            return getSelfLink((Artifact) entity);
        } else if (entity instanceof Contract) {
            return getSelfLink((Contract) entity);
        } else if (entity instanceof ContractRule) {
            return getSelfLink((ContractRule) entity);
        } else if (entity instanceof Agreement) {
            return getSelfLink((Agreement) entity);
        }

        throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
    }

    /**
     * Get self-link from abstract entity.
     *
     * @param entity    The entity.
     * @param describer The entity view assembler.
     * @param <T>       The type of the entity.
     * @param <S>       The type of the assembler.
     * @return The abstract entity and its self-link.
     * @throws ResourceNotFoundException If the entity could not be found.
     */
    public static <T extends AbstractEntity, S extends SelfLinking> URI getSelfLink(
            final T entity, final S describer) throws ResourceNotFoundException {
        try {
            return describer.getSelfLink(entity.getId()).toUri();
        } catch (IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of catalog.
     *
     * @param catalog The catalog.
     * @return The self-link of the catalog.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Catalog catalog) throws ResourceNotFoundException {
        return getSelfLink(catalog, CATALOG_ASSEMBLER);
    }

    /**
     * Get self-link of offered resource.
     *
     * @param resource The offered resource.
     * @return The self-link of the offered resource.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final OfferedResource resource) throws ResourceNotFoundException {
        return getSelfLink(resource, OFFERED_RESOURCE_ASSEMBLER);
    }

    /**
     * Get self-link of requested resource.
     *
     * @param resource The requested resource.
     * @return The self-link of the requested resource.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final RequestedResource resource)
            throws ResourceNotFoundException {
        return getSelfLink(resource, REQUESTED_RESOURCE_ASSEMBLER);
    }

    /**
     * Get self-link of representation.
     *
     * @param representation The representation.
     * @return The self-link of the representation.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Representation representation)
            throws ResourceNotFoundException {
        return getSelfLink(representation, REPRESENTATION_ASSEMBLER);
    }

    /**
     * Get self-link of artifact.
     *
     * @param artifact The artifact.
     * @return The self-link of the artifact.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Artifact artifact) throws ResourceNotFoundException {
        return getSelfLink(artifact, ARTIFACT_ASSEMBLER);
    }

    /**
     * Get self-link of contract.
     *
     * @param contract The contract.
     * @return The self-link of the contract.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Contract contract) throws ResourceNotFoundException {
        return getSelfLink(contract, CONTRACT_ASSEMBLER);
    }

    /**
     * Get self-link of rule.
     *
     * @param rule The rule.
     * @return The self-link of the rule.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final ContractRule rule) throws ResourceNotFoundException {
        return getSelfLink(rule, RULE_ASSEMBLER);
    }

    /**
     * Get self-link of agreement.
     *
     * @param agreement The agreement.
     * @return The self-link of the agreement.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Agreement agreement) throws ResourceNotFoundException {
        return getSelfLink(agreement, AGREEMENT_ASSEMBLER);
    }
}
