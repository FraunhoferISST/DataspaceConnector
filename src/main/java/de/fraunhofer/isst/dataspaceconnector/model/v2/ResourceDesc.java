package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceDesc extends BaseDescription<Resource> {
    private String title;
    private String description;
    private String keywords;
    private String publisher;
    private String language;
    private String licence;
}
