package de.fraunhofer.isst.dataspaceconnector.repositories.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface EndpointRepository extends JpaRepository<Endpoint,
        EndpointId> {
    @Query("select t.id from #{#entityName} t")
    Set<EndpointId> getAllIds();
}
