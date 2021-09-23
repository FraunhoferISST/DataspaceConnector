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
package io.dataspaceconnector.extension.actuator;

import io.dataspaceconnector.extension.actuator.update.UpdateInformationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Expands the actuator-endpoint if enabled and gets release data at runtime
 * on info-endpoint request.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ActuatorController implements InfoContributor {

    /**
     * The project information service.
     */
    private final @NonNull UpdateInformationService updateInformationSvc;

    /**
     * Computes additional data to be added at runtime on actuator-info endpoint request.
     *
     * @param builder The builder that can add additional information to the endpoint.
     */
    @Override
    public void contribute(final Info.Builder builder) {
        try {
            final var information = updateInformationSvc.getUpdateDetails();
            builder.withDetail("connector", information);
        } catch (IOException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to determine if a project update is available."
                        + " [exception=({})]", exception.getMessage());
            }
        }
    }
}
