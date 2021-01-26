package de.fraunhofer.isst.dataspaceconnector.repositories.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EndpointRepository extends JpaRepository<Endpoint,
        EndpointId> {
//    @Query("select t.id from #{#entityName} t")
//    List<UUID> getAllIds();
}
