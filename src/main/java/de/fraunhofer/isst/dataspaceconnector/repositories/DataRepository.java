package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Data;
import de.fraunhofer.isst.dataspaceconnector.model.v2.LocalData;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RemoteData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataRepository extends JpaRepository<Data, Long> {

    @Query("select d from LocalData d")
    List<LocalData> findAllLocalData();

    @Query("select d from RemoteData d")
    List<RemoteData> findAllRemoteData();

}
