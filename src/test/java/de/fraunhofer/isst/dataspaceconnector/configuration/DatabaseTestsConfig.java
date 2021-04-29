package de.fraunhofer.isst.dataspaceconnector.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = {"de.fraunhofer.isst.dataspaceconnector.repositories"})
@EntityScan("de.fraunhofer.isst.dataspaceconnector.model")
@ComponentScan(basePackages = {"de.fraunhofer.isst.dataspaceconnector.repositories",
        "de.fraunhofer.isst.dataspaceconnector.model"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)})
public class DatabaseTestsConfig {
}
