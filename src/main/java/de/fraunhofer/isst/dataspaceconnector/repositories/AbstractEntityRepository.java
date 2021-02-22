package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface AbstractEntityRepository<T extends AbstractEntity> extends JpaRepository<T, UUID> {
    @Query("select t.id from #{#entityName} t")
    List<UUID> getAllIds();

//    @Query("select t.staticId from #{#entityName} t")
//    List<UUID> getAllStaticIds();
//
//    Optional<T> findByStaticId(UUID id);
//    void deleteByStaticId(UUID id);
}
