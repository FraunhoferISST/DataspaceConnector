package io.dataspaceconnector.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = {"io.dataspaceconnector.repositories"})
@EntityScan("io.dataspaceconnector.model")
@ComponentScan(basePackages = {"io.dataspaceconnector.repositories",
        "io.dataspaceconnector.model"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)})
public class DatabaseTestsConfig {
}
