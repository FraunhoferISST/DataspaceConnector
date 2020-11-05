package de.fraunhofer.isst.dataspaceconnector.integrationtest;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.resource.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class tests whether the connecter can give a valid selfdescription.
 *
 * @author Ronja Quensel
 * @version $Id: $Id
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SelfDescriptionTest {
    private final String selfDescriptionEndpoint = "/admin/api/selfservice";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfferedResourceService offeredResourceService;

    @Autowired
    private RequestedResourceService requestedResourceService;

    @Autowired
    private OfferedResourceRepository offeredResourceRepository;

    @Autowired
    private RequestedResourceRepository requestedResourceRepository;

    @Autowired
    private Serializer serializer;

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    public void getSelfDescription_noResources() throws Exception {
        deleteAllResources();

        String response = mockMvc.perform(MockMvcRequestBuilders.get(selfDescriptionEndpoint))
                .andReturn().getResponse().getContentAsString();

        Connector connector = serializer.deserialize(response, Connector.class);

        Assert.assertTrue(connector.getResourceCatalog().get(0).getOfferedResource().isEmpty());
        Assert.assertTrue(connector.getResourceCatalog().get(0).getRequestedResource().isEmpty());
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    public void getSelfDescription_withResources() throws Exception {
        deleteAllResources();

        UUID offeredResourceId = offeredResourceService.addResource(getResourceMetadata());
        offeredResourceService.addData(offeredResourceId, "Hi, I'm data!");

        UUID requestedResourceId = requestedResourceService.addResource(getResourceMetadata());
        requestedResourceService.addData(requestedResourceId, "I'm also data!");

        String response = mockMvc.perform(MockMvcRequestBuilders.get(selfDescriptionEndpoint))
                .andReturn().getResponse().getContentAsString();

        Connector connector = serializer.deserialize(response, Connector.class);
        Assert.assertEquals(1, connector.getResourceCatalog().get(0).getOfferedResource().size());
        Assert.assertEquals(1, connector.getResourceCatalog().get(0).getRequestedResource().size());
    }

    private ResourceMetadata getResourceMetadata() {
        return new ResourceMetadata("Test resource", "", Arrays.asList("test", "resource"), "policy",
                URI.create("http://resource-owner.com"), URI.create("http://license.com"), "v1.0",
                new ArrayList<>());
    }

    private void deleteAllResources() throws Exception {
        offeredResourceRepository.deleteAll();
        requestedResourceRepository.deleteAll();

        Field offeredResourcesMapField = OfferedResourceServiceImpl.class.getDeclaredField("offeredResources");
        offeredResourcesMapField.setAccessible(true);
        offeredResourcesMapField.set(offeredResourceService, new HashMap<UUID, Resource>());

        Field requestedResourcesMapField = RequestedResourceServiceImpl.class.getDeclaredField("requestedResources");
        requestedResourcesMapField.setAccessible(true);
        requestedResourcesMapField.set(requestedResourceService, new HashMap<UUID, Resource>());
    }

}
