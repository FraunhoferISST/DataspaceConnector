package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import java.net.URI;

import org.springframework.stereotype.Component;

@Component
public class AlwaysAllowAccessVerifier implements PolicyVerifier<URI> {
    @Override
    public VerificationResult verify(final URI target) {
        return VerificationResult.ALLOWED;
    }
}
