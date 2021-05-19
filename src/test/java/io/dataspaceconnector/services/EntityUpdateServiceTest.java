package io.dataspaceconnector.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.model.Agreement;
import io.dataspaceconnector.model.ArtifactImpl;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.model.RequestedResourceFactory;
import io.dataspaceconnector.services.ids.updater.ArtifactUpdater;
import io.dataspaceconnector.services.ids.updater.RepresentationUpdater;
import io.dataspaceconnector.services.ids.updater.RequestedResourceUpdater;
import io.dataspaceconnector.services.resources.AgreementService;
import io.dataspaceconnector.services.resources.ArtifactService;
import io.dataspaceconnector.services.resources.RelationServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {EntityUpdateService.class, RequestedResourceFactory.class})
public class EntityUpdateServiceTest {

    @MockBean
    private RequestedResourceUpdater requestedResourceUpdater;

    @MockBean
    private RepresentationUpdater representationUpdater;

    @MockBean
    private ArtifactUpdater artifactUpdater;

    @MockBean
    private AgreementService agreementService;

    @MockBean
    private RelationServices.AgreementArtifactLinker agreementArtifactLinker;

    @MockBean
    private ArtifactService artifactService;

    @Autowired
    private EntityUpdateService entityUpdateService;

    @Autowired
    private RequestedResourceFactory requestedResourceFactory;

    @Test
    public void updateResource_inputNull_throwNullPointerException() {
        /* ARRANGE */
        when(requestedResourceUpdater.update(any())).thenCallRealMethod();

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> entityUpdateService.updateResource(null));
    }

    @Test
    public void updateResource_validInput_updateResourceAndChildren() {
        /* ARRANGE */
        final var resource = getResource();
        final var representation = resource.getRepresentation().get(0);
        final var artifact = representation.getInstance().get(0);

        when(requestedResourceUpdater.update(any())).thenReturn(getRequestedResource());
        when(representationUpdater.update(any()))
                .thenReturn(new io.dataspaceconnector.model.Representation());
        when(artifactUpdater.update(any())).thenReturn(new ArtifactImpl());

        /* ACT */
        entityUpdateService.updateResource(resource);

        /* ASSERT */
        verify(requestedResourceUpdater, times(1)).update(resource);
        verify(representationUpdater, times(1)).update(representation);
        verify(artifactUpdater, times(1)).update((Artifact) artifact);
    }

    @Test
    public void updateResource_resourceNotFound_doNothing() {
        /* ARRANGE */
        final var resource = getResource();

        when(requestedResourceUpdater.update(any())).thenThrow(ResourceNotFoundException.class);

        /* ACT */
        entityUpdateService.updateResource(resource);

        /* ASSERT */
        verify(representationUpdater, never()).update(any());
        verify(artifactUpdater, never()).update(any());
    }

    @Test
    public void updateResource_representationNotFound_doNothing() {
        /* ARRANGE */
        final var resource = getResource();

        when(requestedResourceUpdater.update(any())).thenReturn(getRequestedResource());
        when(representationUpdater.update(any())).thenThrow(ResourceNotFoundException.class);

        /* ACT */
        entityUpdateService.updateResource(resource);

        /* ASSERT */
        verify(artifactUpdater, never()).update(any());
    }

    @Test
    public void confirmAgreement_inputNull_throwNullPointerException() {
        /* ARRANGE */
        when(agreementService.confirmAgreement(any())).thenCallRealMethod();

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> entityUpdateService.confirmAgreement(null));
    }

    @Test
    public void confirmAgreement_confirmationSuccessful_returnTrue() {
        /* ARRANGE */
        final var agreement = new Agreement();

        when(agreementService.confirmAgreement(agreement)).thenReturn(true);

        /* ACT */
        final var result = entityUpdateService.confirmAgreement(agreement);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void confirmAgreement_confirmationUnsuccessful_returnFalse() {
        /* ARRANGE */
        final var agreement = new Agreement();

        when(agreementService.confirmAgreement(agreement)).thenReturn(false);

        /* ACT */
        final var result = entityUpdateService.confirmAgreement(agreement);

        /* ASSERT */
        assertFalse(result);
    }

    @Test
    public void confirmAgreement_resourceNotFound_returnFalse() {
        /* ARRANGE */
        final var agreement = new Agreement();

        when(agreementService.confirmAgreement(agreement))
                .thenThrow(ResourceNotFoundException.class);

        /* ACT */
        final var result = entityUpdateService.confirmAgreement(agreement);

        /* ASSERT */
        assertFalse(result);
    }

    @Test
    public void linkArtifactToAgreement_artifactIdsNull_addEmptySetToAgreements() {
        /* ARRANGE */
        final var agreementId = UUID.randomUUID();

        doNothing().when(agreementArtifactLinker).add(any(), any());

        /* ACT */
        entityUpdateService.linkArtifactToAgreement(null, agreementId);

        /* ASSERT */
        verify(agreementArtifactLinker, times(1))
                .add(agreementId, Collections.emptySet());
    }

    @Test
    public void linkArtifactToAgreement_agreementIdNull_throwIllegalArgumentException() {
        /* ARRANGE */
        final var artifactIds = new ArrayList<URI>();

        doCallRealMethod().when(agreementArtifactLinker).add(any(), any());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> entityUpdateService
                .linkArtifactToAgreement(artifactIds, null));
    }

    @Test
    public void linkArtifactToAgreement_validInput_addArtifactsToAgreement() {
        /* ARRANGE */
        final var artifactId1 = UUID.randomUUID();
        final var artifactId2 = UUID.randomUUID();
        final var agreementId = UUID.randomUUID();

        final var remoteId1 = URI.create("https://artifact.com/1");
        final var remoteId2 = URI.create("https://artifact.com/2");

        when(artifactService.identifyByRemoteId(remoteId1)).thenReturn(Optional.of(artifactId1));
        when(artifactService.identifyByRemoteId(remoteId2)).thenReturn(Optional.of(artifactId2));
        doNothing().when(agreementArtifactLinker).add(any(), any());

        /* ACT */
        entityUpdateService.linkArtifactToAgreement(List.of(remoteId1, remoteId2), agreementId);

        /* ASSERT */
        verify(agreementArtifactLinker, times(1))
                .add(eq(agreementId), eq(Set.of(artifactId1, artifactId2)));
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private Resource getResource() {
        return new ResourceBuilder(URI.create("https://resource-id.com"))
                ._representation_(Util.asList(getRepresentation()))
                .build();
    }

    private Representation getRepresentation() {
        return new RepresentationBuilder(URI.create("https://representation-id.com"))
                ._instance_(Util.asList(getArtifact()))
                .build();
    }

    private Artifact getArtifact() {
        return new ArtifactBuilder(URI.create("https://artifact-id.com"))
                .build();
    }

    private RequestedResource getRequestedResource() {
        return requestedResourceFactory.create(new RequestedResourceDesc());
    }

}
