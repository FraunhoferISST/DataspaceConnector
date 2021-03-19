package de.fraunhofer.isst.dataspaceconnector.utils;

public final class EntityApiBridge {
    private EntityApiBridge(){
        // not used
    }

//    public static ResourceTemplate<OfferedResourceDesc> toOfferedResourceTemplate(final UUID uuid, final ResourceMetadata resourceMetadata) {
//        // TODO: template typedef cleanup
//        final var template = new ResourceTemplate<OfferedResourceDesc>();
//        template.setDesc(toOfferedResourceDesc(uuid, resourceMetadata));
//        template.setRepresentations(new ArrayList<>());
//        template.setContracts(new ArrayList<>());
//
//        if(resourceMetadata.getRepresentations() != null) {
//            for (final var representation : resourceMetadata.getRepresentations().values()) {
//                template.getRepresentations().add(toRepresentationTemplate(representation));
//            }
//        }
//
//        if(resourceMetadata.getPolicy() != null) {
//            template.getContracts().add(toContractTemplate(resourceMetadata.getPolicy()));
//        }
//
//        return template;
//    }
//
//    public static ResourceTemplate<RequestedResourceDesc> toRequestedResourceTemplate(final UUID uuid, final ResourceMetadata resourceMetadata) {
//        // TODO: template typedef cleanup
//        final var template = new ResourceTemplate<RequestedResourceDesc>();
//        template.setDesc(toRequestedResourceDesc(uuid, resourceMetadata));
//        template.setRepresentations(new ArrayList<>());
//        template.setContracts(new ArrayList<>());
//
//        if(resourceMetadata.getRepresentations() != null) {
//            for (final var representation : resourceMetadata.getRepresentations().values()) {
//                template.getRepresentations().add(toRepresentationTemplate(representation));
//            }
//        }
//
//        if(resourceMetadata.getPolicy() != null) {
//            template.getContracts().add(toContractTemplate(resourceMetadata.getPolicy()));
//        }
//
//        return template;
//    }
//
//    public static RepresentationTemplate toRepresentationTemplate(final ResourceRepresentation representation) {
//        final var template = new RepresentationTemplate();
//        template.setDesc(toRepresentationDesc(representation));
//        template.setArtifacts(new ArrayList<>());
//
//        if(representation.getSource() != null) {
//            template.getArtifacts().add(toArtifactTemplate(representation.getSource()));
//        }
//
//        return template;
//    }
//
//    public static ArtifactTemplate toArtifactTemplate(final BackendSource backendSource) {
//        final var template = new ArtifactTemplate();
//        template.setDesc(toArtifactDesc(backendSource));
//
//        return template;
//    }
//
//    public static ContractTemplate toContractTemplate(final String policy) {
//        final var template = new ContractTemplate();
//        template.setDesc(toContractDesc());
//        template.setRules(new ArrayList<>());
//
//        if(policy != null) {
//            template.getRules().add(toRuleTemplate(policy));
//        }
//
//        return template;
//    }
//
//    public static RuleTemplate toRuleTemplate(final String policy) {
//        final var template = new RuleTemplate();
//        template.setDesc(toRuleDesc(policy));
//
//        return template;
//    }
//
//    public static OfferedResourceDesc toOfferedResourceDesc(final UUID uuid, final ResourceMetadata resourceMetadata) {
//        final var desc =  new OfferedResourceDesc();
//        desc.setTitle(resourceMetadata.getTitle());
//        desc.setDescription(resourceMetadata.getDescription());
//        desc.setKeywords(resourceMetadata.getKeywords());
//        desc.setLanguage(null);
//        desc.setLicence(resourceMetadata.getLicense());
//        desc.setStaticId(uuid);
//        desc.setPublisher(resourceMetadata.getOwner());
//
//        return desc;
//    }
//
//    public static RequestedResourceDesc toRequestedResourceDesc(final UUID uuid, final ResourceMetadata resourceMetadata) {
//        final var desc =  new RequestedResourceDesc();
//        desc.setTitle(resourceMetadata.getTitle());
//        desc.setDescription(resourceMetadata.getDescription());
//        desc.setKeywords(resourceMetadata.getKeywords());
//        desc.setLanguage(null);
//        desc.setLicence(resourceMetadata.getLicense());
//        desc.setStaticId(uuid);
//        desc.setPublisher(resourceMetadata.getOwner());
//
//        return desc;
//    }
//
//    public static RepresentationDesc toRepresentationDesc(final ResourceRepresentation representation) {
//        final var desc = new RepresentationDesc();
//        desc.setTitle(representation.getName());
//        desc.setLanguage(null);
//        desc.setStaticId(representation.getUuid());
//        desc.setType(representation.getType());
//
//        return desc;
//    }
//
//    public static ContractDesc toContractDesc() {
//        final var desc = new ContractDesc();
//        desc.setTitle(null);
//        desc.setStaticId(null);
//
//        return desc;
//    }
//
//    public static ArtifactDesc toArtifactDesc(final BackendSource backendSource) {
//        final var desc = new ArtifactDesc();
//        desc.setTitle(null);
//        //desc.setAccessUrl(backendSource.getUrl());
//        desc.setUsername(backendSource.getUsername());
//        desc.setPassword(backendSource.getPassword());
//
//        return desc;
//    }
//
//    public static ContractRuleDesc toRuleDesc(final String policy) {
//        final var desc = new ContractRuleDesc();
//        desc.setTitle(null);
//        desc.setStaticId(null);
//        desc.setValue(policy);
//
//        return desc;
//    }
}
