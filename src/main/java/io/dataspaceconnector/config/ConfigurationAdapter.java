package io.dataspaceconnector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * This class configures admin rights for all backend endpoints behind "/api" using the role
 * defined in {@link MultipleEntryPointsSecurityConfig}.
 */
@Configuration
public class ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                .antMatchers("/api/ids/data").anonymous()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .antMatchers("/database/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint());
        http.headers().frameOptions().deny();
        http.headers().xssProtection();
    }

    /**
     * Bean with an entry point for the admin realm.
     *
     * @return The authentication entry point for the admin realm.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        final var entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("admin realm");
        return entryPoint;
    }
}
