package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public final class AllowAccessVerifier implements PolicyVerifier<URI> {
    @Override
    public VerificationResult verify(final URI input) {
        return VerificationResult.ALLOWED;
    }
}
