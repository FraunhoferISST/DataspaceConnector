package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@NoRepositoryBean
public interface ResourceRepository<T extends Resource> extends BaseResourceRepository<T> {
}

@Repository
interface OfferedResourcesRepository extends ResourceRepository<OfferedResource> {
}

@Repository
interface RequestedResourcesRepository extends ResourceRepository<RequestedResource> {
}
