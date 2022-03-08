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
package io.dataspaceconnector.config.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Configuration for checking if a daps token is valid before entering a function.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PRIVATE)
public class DapsTokenMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    /**
     * Validator for DAT.
     */
    private DapsTokenValidator tokenValidator;

    /**
     * Set the token provider.
     *
     * @param validator The token validator.
     */
    @Autowired
    public void setTokenValidator(final DapsTokenValidator validator) {
        this.tokenValidator = validator;
    }

    /**
     * Create the expression handler using the validator.
     */
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        final var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(tokenValidator);

        return expressionHandler;
    }
}
