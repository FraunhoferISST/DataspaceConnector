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
 * This is the main application class. The application is started and an openApi bean for the
 * Swagger UI is created.
 */
@SpringBootApplication
@ComponentScan({
    "de.fraunhofer.isst.ids.framework.messaging",
    "de.fraunhofer.isst.dataspaceconnector",
    "de.fraunhofer.isst.ids.framework.communication",
    "de.fraunhofer.isst.ids.framework.configuration",
    "de.fraunhofer.isst.ids.framework.daps"
})
public class ConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectorApplication.class, args);
    }

    /**
     * Creates the OpenAPI main description. The description contains general project information
     * such as e.g. title, version and contact information.
     *
     * @return The OpenAPI description.
     * @throws IOException Throws an exception if the properties cannot be loaded from file.
     */
    @Bean
    public OpenAPI customOpenAPI() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader()
            .getResourceAsStream("application.properties")) {
            // This function may crash (e.g. ill-formatted file). Let it bubble up.
            properties.load(inputStream);
        }

        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title(properties.getProperty("title"))
                .description(properties.getProperty("project_desc"))
                .version(properties.getProperty("version"))
                .contact(new Contact()
                    .name(properties.getProperty("organization_name"))
                    .url(properties.getProperty("contact_url"))
                    .email(properties.getProperty("contact_email"))
                )
                .license(new License()
                    .name(properties.getProperty("licence"))
                    .url(properties.getProperty("licence_url")))
            );
    }
}
