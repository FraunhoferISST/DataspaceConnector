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

import io.dataspaceconnector.extension.migration.repositories.AgreementMigrationRepository;
import io.dataspaceconnector.extension.migration.repositories.DataMigrationRepository;
import io.dataspaceconnector.extension.migration.repositories.OfferedResourcesMigrationRepository;
import io.dataspaceconnector.extension.migration.repositories.RequestedResourcesMigrationRepository;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.repository.AuthenticationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
@Transactional
@RequiredArgsConstructor
public class Migrator {
    private final @NonNull OfferedResourcesMigrationRepository   offeredResourcesRepository;
    private final @NonNull RequestedResourcesMigrationRepository requestedResourcesRepository;
    private final @NonNull DataMigrationRepository               dataRepository;
    private final @NonNull AuthenticationRepository     authRepo;
    private final @NonNull AgreementMigrationRepository agreementRepo;

    public void migrate() {
        offeredResourcesRepository.migrateV5ToV6();
        requestedResourcesRepository.migrateV5ToV6();
        migrateAllRemoteData();
        migrateAgreementIDSVersion();
    }

    private void migrateAllRemoteData() {
        for (final var data : dataRepository.findAll()) {
            if (data instanceof RemoteData) {
                migrateRemoteData((RemoteData) data);
            }
        }
    }

    private void migrateRemoteData(final RemoteData remoteData) {
        final var id = remoteData.getId();
        final var user = dataRepository.migrateV5ToV6_getUsername(id);
        final var pwd = dataRepository.migrateV5Tov6_getPassword(id);

        if (user != null && pwd != null) {
            remoteData.addAuthentication(new BasicAuth(user, pwd));
            remoteData.getAuthentication().forEach(authRepo::saveAndFlush);
            dataRepository.saveAndFlush(remoteData);
            dataRepository.migrateV5Tov6_removeUsernameAndPassword(id);
        } else {
            if (log.isWarnEnabled()) {
               log.warn("Data not eligible for migration. [id={}]", id);
            }
        }
    }

    private void migrateAgreementIDSVersion() {
        for (final var ag : agreementRepo.findAll()) {
            if (ag.getValue() != null) {
                final var val = ag.getValue()
                                  .replace("idsc:", "https://w3id.org/idsa/code/");
                agreementRepo.migrateV5ToV6_upgradeIDS(ag.getId(), val);
            }
        }
    }
}
