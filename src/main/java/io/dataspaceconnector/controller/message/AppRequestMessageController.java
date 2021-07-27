package io.dataspaceconnector.controller.message;

import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.MetadataDownloader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * This controller provides the endpoint for sending an app request message and starting the
 * metadata and data exchange.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "Messages", description = "Endpoints for invoke sending messages")
public class AppRequestMessageController {

    /**
     * Downloads metadata.
     */
    private final @NonNull MetadataDownloader metadataDownloader;

    @PostMapping("/app")
    @Operation(summary = "Send IDS App request message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> sendMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The app url.", required = true)
            @RequestParam("app") final URI app) {

        downloadApp(recipient, app);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    private void downloadApp(final URI recipient, final URI appResource) {
//        metadataDownloader.downloadAppResource(recipient, appResource);
    }
}
