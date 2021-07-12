package io.dataspaceconnector.model.broker;

import io.dataspaceconnector.model.base.RegistrationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrokerFactoryTest {

    final BrokerDesc desc = new BrokerDesc();
    final BrokerFactory factory = new BrokerFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var title = "MyBroker";
        desc.setTitle(title);
        desc.setStatus(RegistrationStatus.UNREGISTERED);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(title, result.getTitle());
        assertEquals(RegistrationStatus.UNREGISTERED, result.getStatus());
    }
}
