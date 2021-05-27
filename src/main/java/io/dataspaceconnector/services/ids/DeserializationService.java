/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResponseMessage;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service class for ids object deserialization.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class DeserializationService {

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider serializerProvider;

    /**
     * Deserialize string to ids configuration model.
     *
     * @param config The configuration model string.
     * @return The ids configuration model.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ConfigurationModel getConfigurationModel(final String config)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(config, ConfigurationModel.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize config model. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize input.", e);
        }
    }

    /**
     * Deserialize string to ids infrastructure component.
     *
     * @param component The infrastructure component string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public InfrastructureComponent getInfrastructureComponent(final String component)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(component,
                    InfrastructureComponent.class);
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not deserialize component. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize input.", e);
        }
    }

    /**
     * Deserialize string to ids resource.
     *
     * @param resource The resource string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Resource getResource(final String resource) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(resource, Resource.class);
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not deserialize resource. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize input.", e);
        }
    }

    /**
     * Returns the ids header of an http multipart response if the header is of type
     * ResponseMessage.
     *
     * @param response An ids response message.
     * @return The response message.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ResponseMessage getResponseMessage(final String response)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(response, ResponseMessage.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize response message. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize response message.", e);
        }
    }

    /**
     * Deserialize string to ids message.
     *
     * @param response An ids message.
     * @return The message.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Message getMessage(final String response) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(response, Message.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize message. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize message.", e);
        }
    }

    /**
     * Deserialize string to ids rule.
     *
     * @param policy The policy string.
     * @return The ids rule.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Rule getRule(final String policy) throws IllegalArgumentException {
        return getRule(policy, Rule.class);
    }

    /**
     * Deserialize string to ids object of type rule.
     *
     * @param policy The policy string.
     * @param tClass The Infomodel class.
     * @param <T>    The class type.
     * @return An ids object of type rule.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public <T extends Rule> T getRule(final String policy, final Class<T> tClass)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(policy, tClass);
        } catch (IOException exception) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize rule. [exception=({})]", exception.getMessage());
            }
            throw new IllegalArgumentException("Could not deserialize rule.", exception);
        }
    }

    /**
     * Check if a string is of type ids rule.
     *
     * @param policy The policy string.
     * @param tClass The Infomodel class.
     * @param <T>    The class type.
     * @return False if the matching fails, true if not.
     */
    public <T extends Rule> boolean isRuleType(final String policy, final Class<T> tClass) {
        var isType = false;
        try {
            serializerProvider.getSerializer().deserialize(policy, tClass);
            isType = true;
        } catch (IOException ignore) {
            // Intentionally empty
        }

        return isType;
    }

    /**
     * Deserialize string to ids contract agreement.
     *
     * @param contract The contract string.
     * @return The ids contract agreement.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ContractAgreement getContractAgreement(final String contract)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract,
                    ContractAgreement.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize agreement. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize contract agreement.", e);
        }
    }

    /**
     * Deserialize string to ids contract request.
     *
     * @param contract The contract string.
     * @return The ids contract request.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ContractRequest getContractRequest(final String contract)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, ContractRequest.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize request. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize contract request.", e);
        }
    }
}
