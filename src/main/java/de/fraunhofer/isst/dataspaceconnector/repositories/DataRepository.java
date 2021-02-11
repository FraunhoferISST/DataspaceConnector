package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<Data, Long> {
}
