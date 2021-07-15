package io.dataspaceconnector.view.endpoint;

import io.dataspaceconnector.controller.configurations.EndpointControllers;
import io.dataspaceconnector.controller.resource.view.ViewAssemblerHelper;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointDesc;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConnectorEndpointViewAssemblerTest {

    @Test
    public void create_ValidConnectorEndpoint_returnConnectorEndpointView() {
        /* ARRANGE */
        final var shouldLookLike = getConnectorEndpoint();
        final var link = ViewAssemblerHelper.
                getSelfLink(shouldLookLike.getId(),
                        EndpointControllers.GenericEndpointController.class);

        /* ACT */
        final var after = getConnectorEndpointView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertFalse(after.getLinks().isEmpty());
        assertTrue(after.getLinks().contains(link));
    }

    private ConnectorEndpoint getConnectorEndpoint() {
        final var factory = new ConnectorEndpointFactory();
        return factory.create(getConnectorEndpointDesc());
    }

    private ConnectorEndpointDesc getConnectorEndpointDesc() {
        final var desc = new ConnectorEndpointDesc();
        desc.setLocation(URI.create("https://ids.com"));
        return desc;
    }

    private ConnectorEndpointView getConnectorEndpointView() {
        final var assembler = new ConnectorEndpointViewAssembler();
        return assembler.toModel(getConnectorEndpoint());
    }
}
