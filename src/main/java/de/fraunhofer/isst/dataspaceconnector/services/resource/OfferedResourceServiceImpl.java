package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.HttpUtils;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This class implements all methods of {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService}. It provides database resource handling for all offered resources.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class OfferedResourceServiceImpl implements OfferedResourceService {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(OfferedResourceServiceImpl.class);

    private OfferedResourceRepository offeredResourceRepository;
    private HttpUtils httpUtils;
    private IdsUtils idsUtils;

    private Map<UUID, Resource> offeredResources;
    private ContractOffer contractOffer;

    @Autowired
    /**
     * <p>Constructor for OfferedResourceServiceImpl.</p>
     *
     * @param offeredResourceRepository a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceRepository} object.
     * @param httpUtils a {@link de.fraunhofer.isst.dataspaceconnector.services.HttpUtils} object.
     * @param idsUtils a {@link de.fraunhofer.isst.dataspaceconnector.services.IdsUtils} object.
     */
    public OfferedResourceServiceImpl(OfferedResourceRepository offeredResourceRepository, HttpUtils httpUtils, IdsUtils idsUtils) {
        this.offeredResourceRepository = offeredResourceRepository;
        this.httpUtils = httpUtils;
        this.idsUtils = idsUtils;

        contractOffer = new ContractOfferBuilder()
                ._permission_(Util.asList(new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("provide-access")))
                        ._action_(Util.asList(Action.USE))
                        .build()))
                .build();

        offeredResources = new HashMap<>();
        for (OfferedResource resource : offeredResourceRepository.findAll()) {
            offeredResources.put(resource.getUuid(), idsUtils.getAsResource(resource));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the resource list.
     */
    @Override
    public ArrayList<Resource> getResourceList() {
        return new ArrayList<>(offeredResources.values());
    }

    /** {@inheritDoc} */
    @Override
    public Map<UUID, Resource> getOfferedResources() {
        return offeredResources;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves the resources with its metadata as external resource or internal resource.
     */
    @Override
    public UUID addResource(ResourceMetadata resourceMetadata) {
        resourceMetadata.setPolicy(contractOffer.toRdf());
        OfferedResource resource = new OfferedResource(createUuid(), new Date(), new Date(), resourceMetadata, "");

        offeredResourceRepository.save(resource);
        offeredResources.put(resource.getUuid(), idsUtils.getAsResource(resource));

        return resource.getUuid();
    }

    @Override
    public void addResourceWithId(ResourceMetadata resourceMetadata, UUID uuid) {
        resourceMetadata.setPolicy(contractOffer.toRdf());
        OfferedResource resource = new OfferedResource(uuid, new Date(), new Date(), resourceMetadata, "");

        offeredResourceRepository.save(resource);
        offeredResources.put(resource.getUuid(), idsUtils.getAsResource(resource));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Publishes the resource data.
     */
    @Override
    public void addData(UUID resourceId, String data) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);

        resource.setData(data);
        resource.setModified(new Date());

        offeredResourceRepository.save(resource);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates resource metadata by id.
     */
    @Override
    public void updateResource(UUID resourceId, ResourceMetadata resourceMetadata) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);

        resource.setModified(new Date());
        resource.setResourceMetadata(resourceMetadata);

        offeredResourceRepository.save(resource);
        offeredResources.put(resourceId, idsUtils.getAsResource(resource));
    }

    /** {@inheritDoc} */
    @Override
    public void updateContract(UUID resourceId, String policy) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);
        resource.getResourceMetadata().setPolicy(policy);

        offeredResourceRepository.save(resource);
        offeredResources.put(resourceId, idsUtils.getAsResource(resource));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Deletes a resource by id.
     */
    @Override
    public void deleteResource(UUID resourceId) {
        offeredResourceRepository.deleteById(resourceId);
        offeredResources.remove(resourceId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets a resource by id.
     */
    @Override
    public OfferedResource getResource(UUID resourceId) {
        return offeredResourceRepository.getOne(resourceId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets resource metadata by id.
     */
    @Override
    public ResourceMetadata getMetadata(UUID resourceId) {
        return offeredResourceRepository.getOne(resourceId).getResourceMetadata();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets data from local database.
     */
    @Override
    public String getData(UUID resourceId) throws Exception {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);

        return getDataString(resource, resource.getResourceMetadata().getRepresentations().get(0));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets data from local or external data source.
     */
    @Override
    public String getDataByRepresentation(UUID resourceId, UUID representationId) throws Exception {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);

        String data = "";
        for (ResourceRepresentation representation : resource.getResourceMetadata().getRepresentations()) {
            if (representation.getUuid().equals(representationId)) {
                data = getDataString(resource, representation);
            }
        }
        return data;
    }

    /** {@inheritDoc} */
    @Override
    public UUID addRepresentation(UUID resourceId, ResourceRepresentation representation) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);
        resource.setModified(new Date());

        if (resource.getResourceMetadata().getRepresentations() == null) {
            resource.getResourceMetadata().setRepresentations(new ArrayList<>());
        }
        representation.setUuid(UUID.randomUUID());
        resource.getResourceMetadata().getRepresentations().add(representation);

        offeredResourceRepository.save(resource);
        offeredResources.put(resourceId, idsUtils.getAsResource(resource));

        return representation.getUuid();
    }

    /** {@inheritDoc} */
    @Override
    public UUID addRepresentationWithId(UUID resourceId, ResourceRepresentation representation, UUID representationId) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);
        resource.setModified(new Date());

        if (resource.getResourceMetadata().getRepresentations() == null) {
            resource.getResourceMetadata().setRepresentations(new ArrayList<>());
        }
        representation.setUuid(representationId);
        resource.getResourceMetadata().getRepresentations().add(representation);

        offeredResourceRepository.save(resource);
        offeredResources.put(resourceId, idsUtils.getAsResource(resource));

        return representation.getUuid();
    }

    /** {@inheritDoc} */
    @Override
    public void updateRepresentation(UUID resourceId, UUID representationId, ResourceRepresentation representation) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);
        resource.setModified(new Date());

        representation.setUuid(representationId);
        resource.getResourceMetadata().getRepresentations().set(getIndex(resource, representationId), representation);

        offeredResourceRepository.save(resource);
        offeredResources.put(resourceId, idsUtils.getAsResource(resource));
    }

    /** {@inheritDoc} */
    @Override
    public ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);

        return resource.getResourceMetadata().getRepresentations().get(getIndex(resource, representationId));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteRepresentation(UUID resourceId, UUID representationId) {
        OfferedResource resource = offeredResourceRepository.getOne(resourceId);

        resource.getResourceMetadata().getRepresentations().remove(getIndex(resource, representationId));

        offeredResourceRepository.save(resource);
        offeredResources.put(resourceId, idsUtils.getAsResource(resource));
    }

    /**
     * Returns the representation index for further operations.
     *
     * @param resource         The resource object.
     * @param representationId The representation id.
     * @return The index
     */
    private int getIndex(OfferedResource resource, UUID representationId) {
        int index = -1;
        for (ResourceRepresentation r : resource.getResourceMetadata().getRepresentations()) {
            if (r.getUuid().toString().equals(representationId.toString())) {
                index = resource.getResourceMetadata().getRepresentations().indexOf(r);
            }
        }
        return index;
    }

    /**
     * Gets data as string.
     *
     * @param resource       The connector resource object.
     * @param representation The representation.
     * @return The string or an exception.
     * @throws Exception If the data could not be retrieved.
     */
    private String getDataString(OfferedResource resource, ResourceRepresentation representation) throws Exception {
        if (representation.getSource() != null) {
            String address = representation.getSource().getUrl().toString();
            String username = representation.getSource().getUsername();
            String password = representation.getSource().getPassword();

            switch (representation.getSource().getType()) {
                case LOCAL:
                    return resource.getData();
                case HTTP_GET:
                    return httpUtils.sendHttpGetRequest(address);
                case HTTP_GET_BASICAUTH:
                    return httpUtils.sendHttpGetRequestWithBasicAuth(address, username, password);
                case HTTPS_GET:
                    return httpUtils.sendHttpsGetRequest(address);
                case HTTPS_GET_BASICAUTH:
                    return httpUtils.sendHttpsGetRequestWithBasicAuth(address, username, password);
                case MONGODB:
                    // TODO
                    throw new Exception("Could not retrieve data.");
                default:
                    throw new Exception("Could not retrieve data.");
            }
        } else {
            throw new Exception("Could not retrieve data: backend source missing.");
        }
    }

    /**
     * Generates a unique uuid for a resource, if it does not already exist.
     *
     * @return Generated uuid
     */
    private UUID createUuid() {
        UUID uuid = UUID.randomUUID();
        ArrayList<UUID> list = new ArrayList<>();

        for (OfferedResource r : offeredResourceRepository.findAll()) {
            list.add(r.getUuid());
        }

        if (!list.contains(uuid)) {
            return uuid;
        } else {
            return createUuid();
        }
    }
}
