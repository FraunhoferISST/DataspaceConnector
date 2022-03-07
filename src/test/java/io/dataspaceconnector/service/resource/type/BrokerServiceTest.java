/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.broker.BrokerFactory;
import io.dataspaceconnector.repository.BrokerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { BrokerService.class, BrokerRepository.class, BrokerFactory.class })
class BrokerServiceTest {

    @MockBean
    private BrokerRepository repository;

    @Autowired
    private BrokerService service;

    @Test
    public void findByLocation_knownId_findId() {
        /* ARRANGE */
        final var location = URI.create("https://someLocation");
        final var brokerId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        Mockito.when(repository.findByLocation(location)).thenReturn(Optional.of(brokerId));

        /* ACT */
        final var result = service.findByLocation(location);

        /* ASSERT */
        assertEquals(brokerId, result.get());
    }

    @Test
    public void setRegistrationStatus_validId_setStatus() {
        /* ARRANGE */
        final var location = URI.create("https://someLocation");
        final var status = RegistrationStatus.REGISTERED;

        /* ACT */
        service.setRegistrationStatus(location, status);

        /* ASSERT */
        Mockito.verify(repository, Mockito.atLeastOnce()).setRegistrationStatus(location, status);
    }
}
