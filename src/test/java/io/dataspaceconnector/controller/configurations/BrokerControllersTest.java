package io.dataspaceconnector.controller.configurations;

import io.dataspaceconnector.controller.resource.exception.MethodNotAllowed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BrokerControllersTest {

    @Autowired
    private BrokerControllers.BrokerToOfferedResources brokerToOfferedResources;

    @Test
    public void addResources_validDesc_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> brokerToOfferedResources.addResources(null, null));
    }

    @Test
    public void replaceResources_validDesc_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> brokerToOfferedResources.replaceResources(null, null));
    }

    @Test
    public void removeResources_validDesc_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> brokerToOfferedResources.removeResources(null, null));
    }
}
