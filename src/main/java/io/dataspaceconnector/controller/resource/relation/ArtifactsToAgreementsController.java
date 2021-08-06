package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildRestrictedController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.agreement.AgreementView;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.service.resource.relation.ArtifactAgreementLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between artifacts and agreements.
 */
@RestController
@RequestMapping(BasePath.ARTIFACTS + "/{id}/" + BaseType.AGREEMENTS)
@Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
public class ArtifactsToAgreementsController extends BaseResourceChildRestrictedController<
        ArtifactAgreementLinker, Agreement, AgreementView> {
}
