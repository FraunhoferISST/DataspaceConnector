package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import javax.validation.Valid;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.AgreementView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agreements")
@Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
public class AgreementController extends BaseResourceController<Agreement, AgreementDesc, AgreementView, AgreementService> {
    @Override
    @Hidden
    @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
    public ResponseEntity<AgreementView> create(final AgreementDesc desc) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    @Hidden
    @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
    public ResponseEntity<Object> update(@Valid final UUID resourceId, final AgreementDesc desc) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    @Hidden
    @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
    public ResponseEntity<Void> delete(@Valid final UUID resourceId) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
