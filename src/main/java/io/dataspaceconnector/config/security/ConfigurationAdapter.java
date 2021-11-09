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
package io.dataspaceconnector.config.security;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * This class configures admin rights for all backend endpoints behind "/api" using the role
 * defined in {@link MultipleEntryPointsSecurityConfig}. If the web security is disabled, all
 * DSC APIs are publicly available.
 */
@Configuration
@Getter(AccessLevel.PUBLIC)
@ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true")
@Order(1)
public class ConfigurationAdapter extends WebSecurityConfigurerAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressFBWarnings("SPRING_CSRF_PROTECTION_DISABLED")
    protected final void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/api/ids/data").anonymous()
                .antMatchers("/api/subscriptions/**").authenticated()
                .antMatchers("/api/**").hasRole("ADMIN")
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .antMatchers("/database/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint());
        http.headers().xssProtection();
        http.headers().frameOptions().deny();
    }

    /**
     * Bean with an entry point for the admin realm.
     * @return The authentication entry point for the admin realm.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        final var entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("admin realm");
        return entryPoint;
    }
}
