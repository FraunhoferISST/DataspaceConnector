package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.ZonedDateTime;

/**
 * This class provides access permission information for the {@link RuleValidator} depending on
 * the policy content.
 */
@Service
@RequiredArgsConstructor
public class PolicyInformationService {

    /**
     * Service for handling artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Get creation date of artifact.
     *
     * @param target The target id.
     * @return The artifact's creation date.
     */
    public ZonedDateTime getCreationDate(final URI target) {
        final var resourceId = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(resourceId);

        return artifact.getCreationDate();
    }

    /**
     * Get access number of artifact.
     *
     * @param target The target id.
     * @return The artifact's access number.
     */
    public long getAccessNumber(final URI target) {
        final var resourceId = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(resourceId);

        return artifact.getNumAccessed();
    }
}
