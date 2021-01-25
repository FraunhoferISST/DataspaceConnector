package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;

@Data
public class RepresentationDesc extends BaseDescription<Representation> {
    private String title;
    private String type;
    private String language;
}
