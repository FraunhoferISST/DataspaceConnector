package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.springframework.stereotype.Component;

@Component
public final class AllowAccessVerifier implements PolicyVerifier<Artifact> {
    @Override
    public VerificationResult verify(final Artifact input) {
        return VerificationResult.ALLOWED;
    }
}
