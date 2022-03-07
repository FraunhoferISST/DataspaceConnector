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

import io.dataspaceconnector.model.representation.RepresentationFactory;
import io.dataspaceconnector.repository.RepresentationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RepresentationService.class})
public class RepresentationServiceTest {

    @MockBean
    private RepresentationRepository repository;

    @MockBean
    private RepresentationFactory factory;

    @Autowired
    private RepresentationService service;

    @Test
    public void identifyByRemoteId_inputNull_returnEmptyOptional() {
        /* ARRANGE */
        when(repository.identifyByRemoteId(null)).thenReturn(Optional.empty());

        /* ACT */
        final var result = service.identifyByRemoteId(null);

        /* ASSERT */
        assertTrue(result.isEmpty());
    }

    @Test
    public void identifyByRemoteId_validInput_returnId() {
        /* ARRANGE */
        final var remoteId = URI.create("https://remote-id.com");

        when(repository.identifyByRemoteId(remoteId)).thenReturn(Optional.of(UUID.randomUUID()));

        /* ACT */
        final var result = service.identifyByRemoteId(remoteId);

        /* ASSERT */
        assertTrue(result.isPresent());
    }
}
