package io.dataspaceconnector.services.usagecontrol;

import io.dataspaceconnector.model.Artifact;
import org.springframework.stereotype.Component;

@Component
public final class AllowAccessVerifier implements PolicyVerifier<Artifact> {
    @Override
    public VerificationResult verify(final Artifact input) {
        return VerificationResult.ALLOWED;
    }
}
