package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.RepresentationFactory;
import io.dataspaceconnector.repositories.RepresentationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
