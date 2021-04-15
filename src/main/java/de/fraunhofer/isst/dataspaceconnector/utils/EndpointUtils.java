package de.fraunhofer.isst.dataspaceconnector.utils;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.view.AgreementViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.ArtifactViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.CatalogViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractRuleViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.RequestedResourceViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.services.resources.BasePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class EndpointUtils {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointUtils.class);

    private EndpointUtils() {
        // not used
    }

    /**
     * Determines the current endpoint id from the request context.
     *
     * @param resourceId The resource id passed along the request.
     * @return The Endpoint id.
     */
    public static EndpointId getCurrentEndpoint(final UUID resourceId) {
        var basePath = getCurrentRequestUriBuilder().build().toString();

        final var index = basePath.lastIndexOf(resourceId.toString()) - 1;
        // -1 so that the / gets also removed
        basePath = basePath.substring(0, index);

        return new EndpointId(basePath, resourceId);
    }

    /**
     * Extracts base path and resource id from uri.
     *
     * @param uri The url.
     * @return Endpoint containing base path and resource id (uuid).
     * @throws IllegalArgumentException Failed to extract uuid from string.
     */
    public static EndpointId getEndpointIdFromPath(final URI uri) throws IllegalArgumentException {
        final var fullPath = uri.toString();
        final var allUuids = UUIDUtils.findUuids(fullPath);

        final var resourceId = UUID.fromString(allUuids.get(0));
        final var index = fullPath.lastIndexOf(resourceId.toString()) - 1;
        // -1 so that the / gets also removed
        final var basePath = fullPath.substring(0, index);

        return new EndpointId(basePath, resourceId);
    }

    /**
     * Get current base path as string.
     *
     * @return Base path as string.
     */
    public static String getCurrentBasePathString() {
        final var currentPath = EndpointUtils.getCurrentBasePath();
        return currentPath.toString().substring(0,
                currentPath.toString().indexOf(currentPath.getPath()));
    }

    /**
     * Determines the current base path from the request context.
     *
     * @return The base path as uri.
     */
    private static URI getCurrentBasePath() {
        return getCurrentRequestUriBuilder().build().toUri();
    }

    /**
     * Builds servlet uri from request context.
     *
     * @return The servlet uri component builder.
     */
    private static ServletUriComponentsBuilder getCurrentRequestUriBuilder() {
        return ServletUriComponentsBuilder.fromCurrentRequest();
    }

    /**
     * Get base path enum from base path string.
     *
     * @param path The base path as string.
     * @return The type of base path.
     */
    public static BasePath getBasePathEnumFromString(final String path) {
        try {
            return BasePath.fromString(path);
        } catch (UnreachableLineException exception) {
            return null;
        }
    }

    /**
     * Extract uuid from path url.
     *
     * @param url The url.
     * @return The extracted uuid.
     */
    public static UUID getUUIDFromPath(final URI url) {
        try {
            final var endpoint = EndpointUtils.getEndpointIdFromPath(url);
            return endpoint.getResourceId();
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Could not retrieve uuid from path. [exception=({})]", e.getMessage());
            return null;
        }
    }

    // NOTE This function is a helper function for hiding the problem that the self link is always
    // received through the concrete assembler.
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
     * Get self-link of catalog.
     *
     * @param catalog The catalog.
     * @return The self-link of the catalog.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Catalog catalog) throws ResourceNotFoundException {
        try {
            final var view = new CatalogViewAssembler().toModel(catalog);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of offered resource.
     *
     * @param resource The offered resource.
     * @return The self-link of the offered resource.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final OfferedResource resource) throws ResourceNotFoundException {
        try {
            final var view = new OfferedResourceViewAssembler().toModel(resource);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of requested resource.
     *
     * @param resource The requested resource.
     * @return The self-link of the requested resource.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final RequestedResource resource) throws ResourceNotFoundException {
        try {
            final var view = new RequestedResourceViewAssembler().toModel(resource);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of representation.
     *
     * @param representation The representation.
     * @return The self-link of the representation.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Representation representation) throws ResourceNotFoundException {
        try {
            final var view = new RepresentationViewAssembler().toModel(representation);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of artifact.
     *
     * @param artifact The artifact.
     * @return The self-link of the artifact.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Artifact artifact) throws ResourceNotFoundException {
        try {
            final var view = new ArtifactViewAssembler().toModel(artifact);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of contract.
     *
     * @param contract The contract.
     * @return The self-link of the contract.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Contract contract) throws ResourceNotFoundException {
        try {
            final var view = new ContractViewAssembler().toModel(contract);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of rule.
     *
     * @param rule The rule.
     * @return The self-link of the rule.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final ContractRule rule) throws ResourceNotFoundException {
        try {
            final var view = new ContractRuleViewAssembler().toModel(rule);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of agreement.
     *
     * @param agreement The agreement.
     * @return The self-link of the agreement.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public static URI getSelfLink(final Agreement agreement) throws ResourceNotFoundException {
        try {
            final var view = new AgreementViewAssembler().toModel(agreement);
            return view.getLink("self").get().toUri();
        } catch (NoSuchElementException | IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }
}
