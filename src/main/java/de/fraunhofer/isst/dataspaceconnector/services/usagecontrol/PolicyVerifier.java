package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

public interface PolicyVerifier<T> {
    VerificationResult verify(T target);
}
