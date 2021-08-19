package io.dataspaceconnector.extension.migration.repositories;

import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.repository.ArtifactRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArtifactMigrationRepository extends ArtifactRepository {

    @Query(value = "SELECT DATA_TYPE "
                   + "FROM INFORMATION_SCHEMA.COLUMNS "
                   + "WHERE TABLE_NAME = 'ARTIFACT' "
                   + "AND COLUMN_NAME = 'BOOTSTRAP_ID'",
            nativeQuery = true)
    long getColumnType();

    @Modifying
    @Query(value = "UPDATE ARTIFACT "
                   + "SET BOOTSTRAP_ID_NEW = :value "
                   + "WHERE ID = :id ",
            nativeQuery = true)
    void writeTmpBootstrapId(UUID id, String value);

    @Query(value = "SELECT a.id "
                   + "FROM Artifact a")
    List<UUID> getAllIds();

    @Query(value = "SELECT BOOTSTRAP_ID "
                   + "FROM ARTIFACT "
                   + "WHERE ID = :id",
            nativeQuery = true)
    byte[] getBootstrapId(UUID id);

    @Modifying
    @Query(value = "ALTER TABLE ARTIFACT "
                   + "ALTER COLUMN BOOTSTRAP_ID VARCHAR(2048)",
            nativeQuery = true)
    void dropBootstrapColumn();

    @Modifying
    @Query(value = "ALTER TABLE ARTIFACT "
                   + "RENAME BOOTSTRAP_ID_NEW TO BOOTSTRAP_ID",
    nativeQuery = true)
    void renameTmpBootstrapColumn();

    @Modifying
    @Query(value = "ALTER TABLE ARTIFACT "
                   + "ADD COLUMN BOOTSTRAP_ID_NEW VARCHAR(2048)",
            nativeQuery = true)
    void addBootstrapTmpColumn(long length);
}
