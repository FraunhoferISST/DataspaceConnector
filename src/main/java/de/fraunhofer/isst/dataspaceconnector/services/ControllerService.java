package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.InvalidDynamicAttributeTokenException;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ControllerService {

    /**
     * The token provider.
     */
    private final @NonNull DapsTokenProvider tokenProvider;

    /**
     * Checks if the dat received from the DAPS is emtpy. If yes, an exception is thrown.
     */
    public void checkDynamicAttributeToken()  {
        if (tokenProvider.getDAT() == null) {
            throw new InvalidDynamicAttributeTokenException("Empty DAT.");
        }
    }
}
