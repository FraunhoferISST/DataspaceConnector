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
package io.dataspaceconnector.camel.route.handler;

import io.dataspaceconnector.camel.exception.NoTransferContractException;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.PolicyRestrictionException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for performing policy validation before data provision.
 */
@Component
public class PolicyValidationRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void configure() throws Exception {
        onException(NoTransferContractException.class)
                .to("direct:handleNoTransferContractException");
        onException(ResourceNotFoundException.class, IllegalArgumentException.class)
                .to("direct:handleMessageProcessingFailedForArtifact");
        onException(PolicyRestrictionException.class)
                .to("direct:handlePolicyRestrictionException");
        onException(ContractException.class)
                .to("direct:handleInvalidTransferContract");

        from("direct:policyCheck")
                .routeId("policyCheck")
                .process("PolicyValidator");
    }

}
