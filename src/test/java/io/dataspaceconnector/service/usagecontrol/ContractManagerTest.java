/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.usagecontrol;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.controller.resource.type.ArtifactController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.service.EntityDependencyResolver;
import io.dataspaceconnector.service.EntityResolver;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ContractManager.class})
class ContractManagerTest {

    @Autowired
    private ContractManager contractManager;

    @MockBean
    private EntityResolver entityResolver;

    @MockBean
    private EntityDependencyResolver dependencyResolver;

    @MockBean
    private DeserializationService deserializationService;

    @MockBean(name = "utilSelfLinkHelper")
    private SelfLinkHelper selfLinkHelper;

    @MockBean
    private ConnectorService connectorService;

    @Mock
    private Link artifactSelfLink;

    private final URI agreementId = URI.create("https://agreement");

    private final URI requestedArtifact = URI.create("https://artifact");

    private final URI consumer = URI.create("https://consumer");

    @Test
    void validateTransferContract_valid_returnAgreement() {
        /* ARRANGE */
        final var agreement = getAgreement();
        final var idsAgreement = getIdsAgreement();

        final var artifact = getArtifact();
        final var artifacts = List.of(artifact);

        when(entityResolver.getEntityById(agreementId)).thenReturn(Optional.of(agreement));
        when(dependencyResolver.getArtifactsByAgreement(agreement)).thenReturn(artifacts);
        when(selfLinkHelper.getSelfLink(artifact.getId(), ArtifactController.class))
                .thenReturn(artifactSelfLink);
        when(artifactSelfLink.toUri()).thenReturn(requestedArtifact);
        when(deserializationService.getContractAgreement(agreement.getValue()))
                .thenReturn(idsAgreement);

        /* ACT */
        final var result = contractManager
                .validateTransferContract(agreementId, requestedArtifact, consumer);

        /* ASSERT */
        assertEquals(idsAgreement, result);
    }

    @Test
    void validateTransferContract_agreementNotFound_throwResourceNotFoundException() {
        /* ARRANGE */
        when(entityResolver.getEntityById(agreementId)).thenReturn(Optional.empty());

        /* ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> contractManager
                .validateTransferContract(agreementId, requestedArtifact, consumer));
    }

    @Test
    void validateTransferContract_agreementNotMatchingArtifact_throwContractException() {
        /* ARRANGE */
        final var agreement = getAgreement();

        final var artifact = getArtifact();
        final var artifacts = List.of(artifact);

        final var actualArtifactUri = URI.create("https://not-same-as-artifact");

        when(entityResolver.getEntityById(agreementId)).thenReturn(Optional.of(agreement));
        when(dependencyResolver.getArtifactsByAgreement(agreement)).thenReturn(artifacts);
        when(selfLinkHelper.getSelfLink(artifact.getId(), ArtifactController.class))
                .thenReturn(artifactSelfLink);
        when(artifactSelfLink.toUri()).thenReturn(actualArtifactUri);

        /* ASSERT */
        assertThrows(ContractException.class, () -> contractManager
                .validateTransferContract(agreementId, requestedArtifact, consumer));
    }

    @Test
    void validateTransferContract_agreementNotConfirmed_throwContractException() {
        /* ARRANGE */
        final var agreement = getAgreement();
        ReflectionTestUtils.setField(agreement, "confirmed", false);

        final var artifact = getArtifact();
        final var artifacts = List.of(artifact);

        when(entityResolver.getEntityById(agreementId)).thenReturn(Optional.of(agreement));
        when(dependencyResolver.getArtifactsByAgreement(agreement)).thenReturn(artifacts);
        when(selfLinkHelper.getSelfLink(artifact.getId(), ArtifactController.class))
                .thenReturn(artifactSelfLink);
        when(artifactSelfLink.toUri()).thenReturn(requestedArtifact);

        /* ASSERT */
        assertThrows(ContractException.class, () -> contractManager
                .validateTransferContract(agreementId, requestedArtifact, consumer));
    }

    @Test
    void validateTransferContract_agreementExpired_throwContractException() {
        /* ARRANGE */
        final var agreement = getAgreement();
        final var idsAgreement = getExpiredIdsAgreement();

        final var artifact = getArtifact();
        final var artifacts = List.of(artifact);

        when(entityResolver.getEntityById(agreementId)).thenReturn(Optional.of(agreement));
        when(dependencyResolver.getArtifactsByAgreement(agreement)).thenReturn(artifacts);
        when(selfLinkHelper.getSelfLink(artifact.getId(), ArtifactController.class))
                .thenReturn(artifactSelfLink);
        when(artifactSelfLink.toUri()).thenReturn(requestedArtifact);
        when(deserializationService.getContractAgreement(agreement.getValue()))
                .thenReturn(idsAgreement);

        /* ASSERT */
        assertThrows(ContractException.class, () -> contractManager
                .validateTransferContract(agreementId, requestedArtifact, consumer));
    }

    @Test
    void validateTransferContract_consumerDoesNotMatch_throwContractException() {
        /* ARRANGE */
        final var agreement = getAgreement();
        final var idsAgreement = getIdsAgreementWithInvalidConsumer();

        final var artifact = getArtifact();
        final var artifacts = List.of(artifact);

        when(entityResolver.getEntityById(agreementId)).thenReturn(Optional.of(agreement));
        when(dependencyResolver.getArtifactsByAgreement(agreement)).thenReturn(artifacts);
        when(selfLinkHelper.getSelfLink(artifact.getId(), ArtifactController.class))
                .thenReturn(artifactSelfLink);
        when(artifactSelfLink.toUri()).thenReturn(requestedArtifact);
        when(deserializationService.getContractAgreement(agreement.getValue()))
                .thenReturn(idsAgreement);

        /* ASSERT */
        assertThrows(ContractException.class, () -> contractManager
                .validateTransferContract(agreementId, requestedArtifact, consumer));
    }

    private Artifact getArtifact() {
        return new ArtifactImpl();
    }

    private Agreement getAgreement() {
        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(agreement, "confirmed", true);
        ReflectionTestUtils.setField(agreement, "value", "idsAgreement");
        return agreement;
    }

    private ContractAgreement getIdsAgreement() {
        final var endDate = ZonedDateTime.of(2050, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());

        return new ContractAgreementBuilder()
                ._consumer_(consumer)
                ._contractDate_(IdsMessageUtils.getGregorianNow())
                ._contractStart_(IdsMessageUtils.getGregorianNow())
                ._contractEnd_(ToIdsObjectMapper.getGregorianOf(endDate))
                ._permission_(new PermissionBuilder()._action_(Action.USE).build())
                .build();
    }

    private ContractAgreement getExpiredIdsAgreement() {
        final var endDate = ZonedDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());

        return new ContractAgreementBuilder()
                ._consumer_(consumer)
                ._contractDate_(IdsMessageUtils.getGregorianNow())
                ._contractStart_(IdsMessageUtils.getGregorianNow())
                ._contractEnd_(ToIdsObjectMapper.getGregorianOf(endDate))
                ._permission_(new PermissionBuilder()._action_(Action.USE).build())
                .build();
    }

    private ContractAgreement getIdsAgreementWithInvalidConsumer() {
        final var endDate = ZonedDateTime.of(2050, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());

        return new ContractAgreementBuilder()
                ._consumer_(URI.create("https://other-consumer"))
                ._contractDate_(IdsMessageUtils.getGregorianNow())
                ._contractStart_(IdsMessageUtils.getGregorianNow())
                ._contractEnd_(ToIdsObjectMapper.getGregorianOf(endDate))
                ._permission_(new PermissionBuilder()._action_(Action.USE).build())
                .build();
    }

}
