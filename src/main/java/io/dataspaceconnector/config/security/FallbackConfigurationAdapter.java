package io.dataspaceconnector.config.security;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Getter(AccessLevel.PUBLIC)
@ConditionalOnProperty(value = "spring.security.enabled", havingValue = "false")
@Order(1)
public class FallbackConfigurationAdapter extends WebSecurityConfigurerAdapter {
    @Override
    @SuppressFBWarnings("SPRING_CSRF_PROTECTION_DISABLED")
    protected final void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
        http.headers().xssProtection().disable();
        http.headers().frameOptions().disable();
    }
}
