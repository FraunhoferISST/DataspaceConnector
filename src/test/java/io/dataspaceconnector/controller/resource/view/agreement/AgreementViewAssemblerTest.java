/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.resource.view.agreement;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import io.dataspaceconnector.controller.resource.relation.AgreementsToArtifactsController;
import io.dataspaceconnector.controller.resource.type.AgreementController;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.agreement.AgreementDesc;
import io.dataspaceconnector.model.agreement.AgreementFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {
        AgreementViewAssembler.class
})
public class AgreementViewAssemblerTest {

    @Autowired
    private AgreementViewAssembler agreementViewAssembler;

    @SpyBean
    private AgreementFactory agreementFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = AgreementController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = agreementViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var agreementId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = AgreementController.class
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
        assertThrows(IllegalArgumentException.class, () -> agreementViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnAgreementView() {
        /* ARRANGE */
        final var agreement = getAgreement();

        /* ACT */
        final var result = agreementViewAssembler.toModel(agreement);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(agreement.getValue(), result.getValue());
        Assertions.assertEquals(agreement.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(agreement.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(agreement.getRemoteId(), result.getRemoteId());
        Assertions.assertEquals(agreement.isConfirmed(), result.isConfirmed());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getAgreementLink(agreement.getId()), selfLink.get().getHref());

        final var artifactsLink = result.getLink("artifacts");
        assertTrue(artifactsLink.isPresent());
        assertNotNull(artifactsLink.get());
        assertEquals(getAgreementArtifactsLink(agreement.getId()), artifactsLink.get().getHref());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

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
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = AgreementController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + agreementId;
    }

    private String getAgreementArtifactsLink(final UUID agreementId) {
        return WebMvcLinkBuilder.linkTo(methodOn(AgreementsToArtifactsController.class)
                .getResource(agreementId, null, null)).toString();
    }
}
