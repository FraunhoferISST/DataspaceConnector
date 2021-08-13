/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.extension.migration;

import java.util.ArrayList;
import java.util.List;

import io.dataspaceconnector.extension.migration.repositories.AgreementMigrationRepository;
import io.dataspaceconnector.extension.migration.repositories.DataMigrationRepository;
import io.dataspaceconnector.extension.migration.repositories.OfferedResourcesMigrationRepository;
import io.dataspaceconnector.extension.migration.repositories.RequestedResourcesMigrationRepository;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.auth.Authentication;
import io.dataspaceconnector.repository.AuthenticationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class MigratorTest {

    private OfferedResourcesMigrationRepository offeredResourcesMigrationRepository = Mockito.mock(OfferedResourcesMigrationRepository.class);
    private RequestedResourcesMigrationRepository requestedResourcesMigrationRepository = Mockito.mock(RequestedResourcesMigrationRepository.class);
    private DataMigrationRepository dataRepository = Mockito.mock(DataMigrationRepository.class);
    private AuthenticationRepository authRepo = Mockito.mock(AuthenticationRepository.class);
    private AgreementMigrationRepository agreementRepo = Mockito.mock(AgreementMigrationRepository.class);

    private Migrator migrator = new Migrator(
        offeredResourcesMigrationRepository,
        requestedResourcesMigrationRepository,
        dataRepository,
        authRepo,
        agreementRepo
    );

    @Test
    void migrate() {
        /* ARRANGE */
        final var data = new RemoteData();
        ReflectionTestUtils.setField(data, "authentication", new ArrayList<Authentication>());
        Mockito.doReturn("username").when(dataRepository).migrateV5ToV6GetUsername(Mockito.any());
        Mockito.doReturn("password").when(dataRepository).migrateV5Tov6GetPassword(Mockito.any());
        Mockito.doReturn(List.of(data)).when(dataRepository).findAll();

        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "value", "idsc:USE, but not idsc: https://someContext");
        Mockito.doReturn(List.of(agreement)).when(agreementRepo).findAll();

        /* ACT */
        migrator.migrate();

        /* ASSERT */
        Mockito.verify(offeredResourcesMigrationRepository, Mockito.atLeastOnce()).migrateV5ToV6();
        Mockito.verify(requestedResourcesMigrationRepository, Mockito.atLeastOnce()).migrateV5ToV6();
    }
}
