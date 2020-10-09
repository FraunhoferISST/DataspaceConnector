package de.fraunhofer.isst.dataspaceconnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * This class creates an admin role for spring basic security setup used in {@link de.fraunhofer.isst.dataspaceconnector.config.ConfigurationAdapter}.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Configuration
@EnableWebSecurity
public class MultipleEntryPointsSecurityConfig {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    /**
     * <p>userDetailsService.</p>
     *
     * @return a {@link org.springframework.security.core.userdetails.UserDetailsService} object.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername(username)
                .password(encoder().encode(password))
                .roles("ADMIN").build());
        return manager;
    }

    /**
     * <p>encoder.</p>
     *
     * @return a {@link org.springframework.security.crypto.password.PasswordEncoder} object.
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
