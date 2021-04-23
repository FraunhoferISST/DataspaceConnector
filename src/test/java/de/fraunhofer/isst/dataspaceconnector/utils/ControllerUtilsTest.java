package de.fraunhofer.isst.dataspaceconnector.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ControllerUtils.class})
class ControllerUtilsTest {

    @Test
    public void respondIdsMessageFailed_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Ids message handling failed. "
                + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondIdsMessageFailed(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondReceivedInvalidResponse_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Failed to read the ids response "
                + "message.", HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondReceivedInvalidResponse(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondConfigurationUpdateError_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Failed to update configuration.",
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondConfigurationUpdateError(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondDeserializationError_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Failed to update.",
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondDeserializationError(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondConfigurationNotFound_null_returnValidResponseEntity() {
        /* ARRANGE */
        final var expectedResponse = new ResponseEntity<>("No configuration found.",
                HttpStatus.NOT_FOUND);

        /* ACT */
        final var response = ControllerUtils.respondConfigurationNotFound();

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondDeserializationError_validUri_returnValidResponseEntity() {
        /* ARRANGE */
        final var resourceId = URI.create("https://requestedResource");
        final var expectedResponse = new ResponseEntity<>(String.format("Resource %s not found.",
                resourceId), HttpStatus.NOT_FOUND);

        /* ACT */
        final var response = ControllerUtils.respondResourceNotFound(resourceId);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondResourceCouldNotBeLoaded_validUri_returnValidResponseEntity() {
        /* ARRANGE */
        final var resourceId = URI.create("https://requestedResource");
        final var expectedResponse = new ResponseEntity<>(String.format("Could not load resource " +
                        "%s.",
                resourceId), HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondResourceCouldNotBeLoaded(resourceId);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondPatternNotIdentified_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Could not identify pattern.",
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondPatternNotIdentified(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondInvalidInput_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Invalid input. "
                + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondInvalidInput(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondFailedToBuildContractRequest_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Failed to build contract request.",
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondFailedToBuildContractRequest(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondConnectorNotLoaded_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Connector could not be loaded.",
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondConnectorNotLoaded(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondGlobalException_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Something else went wrong.",
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondGlobalException(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondFailedToStoreEntity_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var exception = new Exception("Some exception.");
        final var expectedResponse = new ResponseEntity<>("Failed to store entity. "
                + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ControllerUtils.respondFailedToStoreEntity(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondWithMessageContent_validMap_returnValidResponseEntity() {
        /* ARRANGE */
        final var obj = new Object();
        final var map = Map.of("header", obj);
        final var expectedResponse = new ResponseEntity<>(map, HttpStatus.EXPECTATION_FAILED);

        /* ACT */
        final var response = ControllerUtils.respondWithMessageContent(map);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

}
