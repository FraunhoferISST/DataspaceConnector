package de.fraunhofer.isst.dataspaceconnector;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is the main application class. The application is started and an openApi bean for the Swagger UI is created.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@SpringBootApplication
@ComponentScan({
        "de.fraunhofer.isst.ids.framework.messaging.spring.controller",
        "de.fraunhofer.isst.ids.framework.messaging.spring",
        "de.fraunhofer.isst.dataspaceconnector",
//        "de.fraunhofer.isst.ids.framework.configurationmanager.controller",
        "de.fraunhofer.isst.ids.framework.spring.starter"
})
public class ConnectorApplication {
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        SpringApplication.run(ConnectorApplication.class, args);
    }

    /**
     * Creates the OpenAPI main description.
     *
     * @throws IOException Throws an exception if the properties cannot be loaded from file.
     * @return The OpenAPI.
     */
    @Bean
    public OpenAPI customOpenAPI() throws IOException {
        Properties properties = new Properties();
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("project.properties")) {
            // This function may crash (e.g. ill-formatted file). Let it bubble up.
            properties.load(inputStream);
        }

        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Dataspace Connector API")
                        .description("This is the Dataspace Connector's backend API using springdoc-openapi and OpenAPI 3.")
                        .version(properties.getProperty("version"))
                        .contact(new Contact()
                                .name("Julia Pampus")
                                .email("julia.pampus@isst.fraunhofer.de")
                        )
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
                );
    }
}
