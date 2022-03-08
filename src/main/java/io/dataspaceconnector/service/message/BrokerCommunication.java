/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.message;

import de.fraunhofer.iais.eis.Resource;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.UUIDFormatException;
import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.service.resource.relation.BrokerOfferedResourceLinker;
import io.dataspaceconnector.service.resource.type.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

/**
 * Service for handling broker logic during ids communication.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerCommunication {

    /**
     * Service for relation between broker and offered resources.
     */
    private final @NotNull BrokerOfferedResourceLinker linker;

    /**
     * Service for the broker.
     */
    private final @NotNull BrokerService brokerService;

    /**
     * Check if input is a broker id or an url.
     *
     * @param input The input uri.
     * @return The location of the broker object or the original uri (url).
     */
    public URI checkInput(final URI input) {
        try {
            final var broker = brokerService.get(UUIDUtils.uuidFromUri(input));
            return broker.getLocation();
        } catch (UUIDFormatException exception) {
            // Input uri is not a broker id. Proceed.
        } catch (ResourceNotFoundException exception) {
            // No broker found for this id. Proceed.
        }
        return input;
    }

    /**
     * Link resource offer to broker entity.
     *
     * @param recipient The uri of the recipient.
     * @param resource  The offered resource.
     */
    public void updateOfferedResourceBrokerList(final URI recipient,
                                                final Resource resource) {
        final var brokerId = brokerService.findByLocation(recipient);
        if (brokerId.isPresent()) {
            linker.add(brokerId.get(), Set.of(UUIDUtils.uuidFromUri(resource.getId())));
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Updated resource at broker. Failed to link broker to offer. "
                        + "[recipient=({}), resource=({})]", recipient, resource.getId());
            }
        }
    }

    /**
     * Remove resource offer from broker entity.
     *
     * @param recipient The uri of the recipient.
     * @param resource  The offered resource.
     */
    public void removeBrokerFromOfferedResourceBrokerList(final URI recipient,
                                                          final Resource resource) {
        final var brokerId = brokerService.findByLocation(recipient);
        if (brokerId.isPresent()) {
            linker.remove(brokerId.get(), Set.of(UUIDUtils.uuidFromUri(resource.getId())));
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Removed resource from broker. Failed to remove link from broker to "
                        + "offer. [recipient=({}), resource=({})]", recipient, resource.getId());
            }
        }
    }

}
