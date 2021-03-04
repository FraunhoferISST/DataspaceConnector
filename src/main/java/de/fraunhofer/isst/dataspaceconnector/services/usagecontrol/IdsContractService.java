package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class IdsContractService {

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider serializerProvider;

    /**
     * Deserialize string to ids contract.
     *
     * @param policy The policy string.
     * @return The ids contract.
     * @throws IOException If deserialization fails.
     */
    public Contract deserializeContract(final String policy) throws IOException {
        return serializerProvider.getSerializer().deserialize(policy, Contract.class);
    }

    /**
     * Deserialize string to ids rule.
     *
     * @param policy The policy string.
     * @return The ids rule.
     * @throws IOException If deserialization fails.
     */
    public Rule deserializeRule(final String policy) throws IOException {
        return serializerProvider.getSerializer().deserialize(policy, Rule.class);
    }
}
