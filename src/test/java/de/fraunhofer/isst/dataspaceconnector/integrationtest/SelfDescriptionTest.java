package de.fraunhofer.isst.dataspaceconnector.integrationtest;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
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
import java.net.URI;
import java.util.*;

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
    private final String selfDescriptionEndpoint = "/admin/api/self-description";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfferedResourceServiceImpl offeredResourceService;

    @Autowired
    private RequestedResourceServiceImpl requestedResourceService;

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
        final var representationId = UUIDUtils.createUUID((UUID x) -> false);
        final var representation = new ResourceRepresentation();
        representation.setUuid(representationId);
        representation.setType("Type");
        representation.setByteSize(1);
        representation.setName("Name");

        final var source = new BackendSource();
        source.setType(BackendSource.Type.LOCAL);

        representation.setSource(source);

        String policy = "{\n" +
                "  \"@context\" : {\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\"\n" +
                "  },\n" +
                "  \"@type\" : \"ids:ContractOffer\",\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/contractOffer/b03ec7da-d208-4dea-91e5-5683703732a9\",\n" +
                "  \"ids:permission\" : [ {\n" +
                "    \"@type\" : \"ids:Permission\",\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/permission/429f3e71-df2c-47f4-80f2-7eff27ed4542\",\n" +
                "    \"ids:action\" : [ {\n" +
                "      \"@id\" : \"idsc:USE\"\n" +
                "    } ],\n" +
                "    \"ids:description\" : [ {\n" +
                "      \"@value\" : \"provide-access\",\n" +
                "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                "    } ],\n" +
                "    \"ids:title\" : [ {\n" +
                "      \"@value\" : \"Example Usage Policy\",\n" +
                "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}";

        return new ResourceMetadata("Test resource", "", Arrays.asList("test", "resource"), policy,
                URI.create("http://resource-owner.com"), URI.create("http://license.com"), "v1.0",
                Collections.singletonMap(representationId, representation));
    }

    private void deleteAllResources() throws Exception {
        offeredResourceRepository.deleteAll();
        requestedResourceRepository.deleteAll();
    }

}
