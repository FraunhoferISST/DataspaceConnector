package de.fraunhofer.isst.dataspaceconnector.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = {"de.fraunhofer.isst.dataspaceconnector.repositories"})
@EntityScan("de.fraunhofer.isst.dataspaceconnector.model")
@ComponentScan(basePackages = {"de.fraunhofer.isst.dataspaceconnector"})
//@ComponentScan(basePackages = {"de.fraunhofer.isst.dataspaceconnector.repositories.v2",
//        "de.fraunhofer.isst.dataspaceconnector.model.v2",
//        "de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend"})
public class DatabaseTestsConfig {
}
