package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceDesc<T extends Resource> extends AbstractDescription<T> {
    private String title;
    private String description;
    private List<String> keywords;
    private URI publisher;
    private String language;
    private URI licence;
}
