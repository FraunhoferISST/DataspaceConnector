package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.AgreementViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
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

@SpringBootTest(classes = {AgreementController.class})
class AgreementControllerTest {
    @MockBean
    private AgreementService service;

    @MockBean
    private AgreementViewAssembler assembler;

    @MockBean
    private PagedResourcesAssembler<Agreement> pagedAssembler;

    @Autowired
    @InjectMocks
    private AgreementController controller;

    /**
     * create.
     */

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
        final var result = controller.create(new AgreementDesc());

        /* ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
        Mockito.verifyNoInteractions(service);
    }

    /**
     * update.
     */
    @Test
    public void update_null_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = controller.update(null, null);

        /* ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void update_validInput_returnMethodNotAllowed() {
        /* ARRANGE */
        final var id = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT */
        final var result = controller.update(id, new AgreementDesc());

        /* ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
        Mockito.verifyNoInteractions(service);
    }

    /**
     * delete.
     */
    @Test
    public void delete_null_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = controller.delete(null);

        /* ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void delete_anyId_returnMethodNotAllowed() {
        /* ARRANGE */
        final var id = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT */
        final var result = controller.delete(id);

        /* ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
        Mockito.verifyNoInteractions(service);
    }
}
