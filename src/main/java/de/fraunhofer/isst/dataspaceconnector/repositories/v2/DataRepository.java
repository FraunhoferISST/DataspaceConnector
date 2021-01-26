package de.fraunhofer.isst.dataspaceconnector.repositories.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<Data, Long> {
}
