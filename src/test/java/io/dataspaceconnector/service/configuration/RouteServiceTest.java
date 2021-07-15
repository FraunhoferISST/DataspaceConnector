package io.dataspaceconnector.service.configuration;

import java.util.UUID;

import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteFactory;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.repository.RouteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { RouteService.class, RouteRepository.class, EndpointRepository.class, RouteFactory.class })
class RouteServiceTest {

    @MockBean
    private EndpointRepository endpointRepository;

    @MockBean
    private RouteRepository repository;

    @Autowired
    private RouteService service;

    @Test
    public void persist_validInput_saveRoutes() {
        /* ARRANGE */
        final var start = new Endpoint();
        ReflectionTestUtils.setField(start, "id", UUID.randomUUID());
        final var end = new Endpoint();
        ReflectionTestUtils.setField(end, "id", UUID.randomUUID());

        final var route = new Route();
        ReflectionTestUtils.setField(route, "start", start);
        ReflectionTestUtils.setField(route, "end", end);

        /* ACT */
        service.persist(route);

        /* ASSERT */
        Mockito.verify(endpointRepository, Mockito.times(2)).save(eq(start));
        Mockito.verify(endpointRepository, Mockito.times(2)).save(eq(end));
    }

}
