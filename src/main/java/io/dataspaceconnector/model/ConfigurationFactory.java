package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class ConfigurationFactory implements AbstractFactory<Configuration, ConfigurationDesc>{

    @Override
    public Configuration create(ConfigurationDesc desc) {
        return null;
    }

    @Override
    public boolean update(Configuration entity, ConfigurationDesc desc) {
        return false;
    }
}
