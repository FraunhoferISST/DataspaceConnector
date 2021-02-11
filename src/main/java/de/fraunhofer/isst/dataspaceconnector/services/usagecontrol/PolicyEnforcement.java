package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.isst.dataspaceconnector.config.PolicyConfiguration;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.repositories.RequestedResourceRepository;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * This class implements automated policy check and usage control enforcement.
 */
@Component
@EnableScheduling
public class PolicyEnforcement {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyEnforcement.class);

    private final PolicyVerifier policyVerifier;
    private final ResourceService resourceService;
    private final RequestedResourceRepository requestedResourceRepository;
    private final SerializerProvider serializerProvider;
    private final PolicyConfiguration policyConfiguration;

    /**
     * Constructor for PolicyEnforcement.
     *
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public PolicyEnforcement(PolicyVerifier policyVerifier,
                             RequestedResourceServiceImpl requestedResourceService,
                             RequestedResourceRepository requestedResourceRepository,
                             PolicyConfiguration policyConfiguration,
                             SerializerProvider serializerProvider) throws IllegalArgumentException {
        if (policyVerifier == null)
            throw new IllegalArgumentException("The PolicyVerifier cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The RequestedResourceServiceImpl cannot be null.");

        if (requestedResourceRepository == null)
            throw new IllegalArgumentException("The RequestedResourceRepository cannot be null.");

        if (policyConfiguration == null)
            throw new IllegalArgumentException("The PolicyConfiguration cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.policyVerifier = policyVerifier;
        this.resourceService = requestedResourceService;
        this.requestedResourceRepository = requestedResourceRepository;
        this.policyConfiguration = policyConfiguration;
        this.serializerProvider = serializerProvider;
    }

    /**
     * Periodically (every minute) calls {@link PolicyEnforcement#checkResources()}.
     * 1000 = 1 sec * 60 * 60 = every hour (3600000)
     */
    @Scheduled(fixedDelay = 60000)
    public void schedule() {
        if (policyConfiguration.getUsageControlFramework() ==
                PolicyConfiguration.UsageControlFramework.INTERNAL) {
            try {
                checkResources();
            } catch (ParseException | IOException exception) {
                LOGGER.warn("Failed to check policy. [exception=({})]", exception.getMessage());
            }
        }
    }

    /**
     * Checks all known resources and their policies to delete them if necessary.
     *
     * @throws java.text.ParseException if a date from a policy cannot be parsed.
     * @throws java.io.IOException if an error occurs while deserializing a contract.
     */
    public void checkResources() throws ParseException, IOException {
        LOGGER.info("Check data...");

        for (RequestedResource resource : requestedResourceRepository.findAll()) {
            String policy = resource.getResourceMetadata().getPolicy();
            try {
                Contract contract = serializerProvider.getSerializer()
                    .deserialize(policy, Contract.class);
                if (contract.getPermission() != null && contract.getPermission().get(0) != null) {
                    Permission permission = contract.getPermission().get(0);
                    ArrayList<? extends Duty> postDuties = permission.getPostDuty();

                    if (postDuties != null && postDuties.get(0) != null) {
                        Action action = postDuties.get(0).getAction().get(0);
                        if (action == Action.DELETE) {
                            if (policyVerifier.checkForDelete(postDuties.get(0))) {
                                resourceService.deleteResource(resource.getUuid());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new IOException(
                    "The policy could not be read. Please check the policy syntax.");
            }
        }
    }
}
