package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.view.RequestedResourceViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {ResourceControllers.RequestedResourceController.class})
class RequestedResourceControllerTest {
    @MockBean
    private ResourceService<RequestedResource, RequestedResourceDesc> service;

    @MockBean
    private RequestedResourceViewAssembler assembler;

    @MockBean
    private PagedResourcesAssembler<RequestedResource> pagedAssembler;

    @Autowired
    @InjectMocks
    private ResourceControllers.RequestedResourceController controller;

    @Test
    public void create_null_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = controller.create(null);

        /* ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void create_validDesc_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = controller.create(new RequestedResourceDesc());

        /* ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
        Mockito.verifyNoInteractions(service);
    }
}
