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
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Configuration
public class ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    /**
     * {@inheritDoc}
     */
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
     * <p>authenticationEntryPoint.</p>
     *
     * @return a {@link org.springframework.security.web.AuthenticationEntryPoint} object.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("admin realm");
        return entryPoint;
    }
}
