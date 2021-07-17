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
package io.dataspaceconnector.service.configuration;

import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.model.configuration.ConfigurationDesc;
import io.dataspaceconnector.repository.ConfigurationRepository;
import io.dataspaceconnector.service.resource.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service class for the configuration.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
@RequiredArgsConstructor
public class ConfigurationService extends BaseEntityService<Configuration, ConfigurationDesc> {

    /**
     * Get all selected configurations.
     *
     * @return List of configurations with selected = true.
     */
    public List<UUID> findSelected() {
        return ((ConfigurationRepository) getRepository()).findBySelectedTrue();
    }

    /**
     * Mark a new configuration as selected.
     *
     * @param newSelected UUID of the configuration to mark as selected
     */
    public void swapSelected(final UUID newSelected) {
        var repo = (ConfigurationRepository) getRepository();
        var selectedId = repo.findBySelectedTrue();
        if (selectedId.size() > 0) {
            if (newSelected.equals(selectedId.get(0))) {
                return;
            }
            repo.deselectCurrent();
        }
        repo.selectById(newSelected);
    }

}
