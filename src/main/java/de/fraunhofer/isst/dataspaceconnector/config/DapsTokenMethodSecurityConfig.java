package de.fraunhofer.isst.dataspaceconnector.config;

import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import de.fraunhofer.isst.ids.framework.daps.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DapsTokenMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private DapsTokenValidator tokenValidator;

    @Autowired
    public void setTokenProvider(final DapsTokenValidator validator) {
        this.tokenValidator = validator;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        final var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(tokenValidator);

        return expressionHandler;
    }
}
