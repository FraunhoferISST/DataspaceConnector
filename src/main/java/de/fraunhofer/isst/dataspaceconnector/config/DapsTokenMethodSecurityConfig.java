package de.fraunhofer.isst.dataspaceconnector.config;

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
public class DapsTokenMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    /**
     * Validator for DAT.
     */
    private DapsTokenValidator tokenValidator;

    /**
     * Set the token provider.
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
