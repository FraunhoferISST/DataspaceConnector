package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

/**
 * Interface for verifying policies.
 *
 * @param <T> Type for the verification input.
 */
public interface PolicyVerifier<T> {

    /**
     * Verify policy base on input.
     *
     * @param input Reference object of verification.
     * @return The verification result.
     */
    VerificationResult verify(T input);
}
