package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@NoRepositoryBean
public interface ResourceRepository<T extends Resource> extends AbstractEntityRepository<T> {
}

@RepositoryRestResource(collectionResourceRel = "offeredresources", path="offeredresources")
interface OfferedResourcesRepository extends ResourceRepository<OfferedResource> {
}

@RepositoryRestResource(collectionResourceRel = "requestedresources", path="requestedresources")
interface RequestedResourcesRepository extends ResourceRepository<RequestedResource> {
}
