package io.dataspaceconnector.service.configuration;

import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.ids.messaging.core.config.ConfigProducer;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.repository.ConfigurationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class ConfigurationServiceTest {

    @SpyBean
    private ConfigurationRepository repo;

    @SpyBean
    private ConfigProducer configProducer;

    @Autowired
    private ConfigurationService service;

    @Test
    public void swapActiveConfig_hasActiveConfig_willSetPassedConfigAsActiveAndTheOldOneAsInActive()
            throws ConfigUpdateException {
        /* ARRANGE */
        final var configId = UUID.randomUUID();
        Mockito.doReturn(Optional.empty()).when(repo).findActive();

        /* ACT */
        service.swapActiveConfig(configId);

        /* ASSERT */
        Mockito.verify(repo, Mockito.atLeastOnce()).setActive(eq(configId));
        Mockito.verify(repo, Mockito.atLeastOnce()).unsetActive();
    }
}
