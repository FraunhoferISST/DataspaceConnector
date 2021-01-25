package de.fraunhofer.isst.dataspaceconnector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * This class configures admin rights for all backend endpoints behind "/admin" using the role
 * defined in {@link de.fraunhofer.isst.dataspaceconnector.config.MultipleEntryPointsSecurityConfig}.
 */
@Configuration
public class ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable().formLogin().disable()
            .antMatcher("/admin/**")
            .authorizeRequests().anyRequest().hasRole("ADMIN")
            .and()
            .httpBasic()
            .authenticationEntryPoint(authenticationEntryPoint());
        http.headers().frameOptions().disable();
    }

    /**
     * Bean with an entry point for the admin realm
     * @return The authentication entry point for the admin realm
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("admin realm");
        return entryPoint;
    }
}
