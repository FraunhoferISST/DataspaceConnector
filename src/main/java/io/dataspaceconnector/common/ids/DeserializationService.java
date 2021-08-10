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
package io.dataspaceconnector.common.ids;

import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.ids.messaging.util.SerializerProvider;
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
    private final @NonNull SerializerProvider serProvider;

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
            return serProvider.getSerializer().deserialize(config, ConfigurationModel.class);
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
            return serProvider.getSerializer().deserialize(component,
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
            return serProvider.getSerializer().deserialize(resource, Resource.class);
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not deserialize resource. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize input.", e);
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
            return serProvider.getSerializer().deserialize(response, Message.class);
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
            return serProvider.getSerializer().deserialize(policy, tClass);
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
            serProvider.getSerializer().deserialize(policy, tClass);
            isType = true;
        } catch (IOException expected) {
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
            return serProvider.getSerializer().deserialize(contract, ContractAgreement.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize agreement. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize contract agreement.", e);
        }
    }

    /**
     * Deserialize string to ids catalog.
     *
     * @param catalog The catalog string.
     * @return The ids catalog.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Catalog getCatalog(final String catalog) throws IllegalArgumentException {
        try {
            return serProvider.getSerializer().deserialize(catalog, Catalog.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize catalog. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize catalog.", e);
        }
    }

    /**
     * Deserialize string to ids resource catalog.
     *
     * @param catalog The catalog string.
     * @return The ids catalog.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ResourceCatalog getResourceCatalog(final String catalog)
            throws IllegalArgumentException {
        try {
            return serProvider.getSerializer().deserialize(catalog, ResourceCatalog.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize resource catalog. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize resource catalog.", e);
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
            return serProvider.getSerializer().deserialize(contract, ContractRequest.class);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not deserialize request. [exception=({})]", e.getMessage(), e);
            }
            throw new IllegalArgumentException("Could not deserialize contract request.", e);
        }
    }
}
