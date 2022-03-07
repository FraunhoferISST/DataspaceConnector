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
package io.dataspaceconnector.extension.monitoring;

import org.apache.camel.CamelContext;
import org.springframework.boot.actuate.info.Info;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Expands the actuator-endpoint if enabled and exposes camel data at runtime at the info-endpoint.
 */
@Component
public class CamelInfoContributorExt
        extends org.apache.camel.spring.boot.actuate.info.CamelInfoContributor {

    /**
     * The project information service.
     */
    private final CamelContext camelContext;

    /**
     * Default constructor with params.
     *
     * @param context The camel context.
     */
    public CamelInfoContributorExt(final CamelContext context) {
        super(context);
        this.camelContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contribute(final Info.Builder builder) {
        if (this.camelContext != null) {
            final var info = new HashMap<String, Object>();

            info.put("name", this.camelContext.getName());
            info.put("version", this.camelContext.getVersion());
            if (this.camelContext.getUptime() != null) {
                info.put("startDate", this.camelContext.getStartDate());
                info.put("uptime", this.camelContext.getUptime());
            }
            info.put("status", this.camelContext.getStatus().name());

            builder.withDetail("camel", info);
        }
    }
}
