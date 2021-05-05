package de.fraunhofer.isst.dataspaceconnector.view;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {AgreementViewAssembler.class, ViewAssemblerHelper.class,
        AgreementFactory.class})
public class AgreementViewAssemblerTest {

    @Autowired
    private AgreementViewAssembler agreementViewAssembler;

    @Autowired
    private AgreementFactory agreementFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.AgreementController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = agreementViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var agreementId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.AgreementController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = agreementViewAssembler.getSelfLink(agreementId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + agreementId, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> agreementViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnAgreementView() {
        /* ARRANGE */
        final var agreement = getAgreement();

        /* ACT */
        final var result = agreementViewAssembler.toModel(agreement);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(agreement.getValue(), result.getValue());
        assertEquals(agreement.getCreationDate(), result.getCreationDate());
        assertEquals(agreement.getModificationDate(), result.getModificationDate());
        assertEquals(agreement.getRemoteId(), result.getRemoteId());
        assertEquals(agreement.isConfirmed(), result.isConfirmed());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getAgreementLink(agreement.getId()), selfLink.get().getHref());

        final var artifactsLink = result.getLink("artifacts");
        assertTrue(artifactsLink.isPresent());
        assertNotNull(artifactsLink.get());
        assertEquals(getAgreementArtifactsLink(agreement.getId()), artifactsLink.get().getHref());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private Agreement getAgreement() {
        final var desc = new AgreementDesc();
        desc.setValue("agreement value");
        desc.setConfirmed(true);
        desc.setRemoteId(URI.create("https://agreement.com"));
        final var agreement = agreementFactory.create(desc);

        final var date = ZonedDateTime.now(ZoneOffset.UTC);

        ReflectionTestUtils.setField(agreement, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(agreement, "creationDate", date);
        ReflectionTestUtils.setField(agreement, "modificationDate", date);

        return agreement;
    }

    private String getAgreementLink(final UUID agreementId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.AgreementController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + agreementId;
    }

    private String getAgreementArtifactsLink(final UUID agreementId) {
        return linkTo(methodOn(RelationControllers.AgreementsToArtifacts.class)
                .getResource(agreementId, null, null, null)).toString();
    }

}
