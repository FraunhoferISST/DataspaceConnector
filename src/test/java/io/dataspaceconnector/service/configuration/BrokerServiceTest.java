package io.dataspaceconnector.service.configuration;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.broker.BrokerFactory;
import io.dataspaceconnector.repository.BrokerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
