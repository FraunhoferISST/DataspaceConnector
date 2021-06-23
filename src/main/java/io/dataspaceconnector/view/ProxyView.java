package io.dataspaceconnector.view;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.view.util.ViewConstants;
import org.springframework.hateoas.RepresentationModel;

public class ProxyView extends RepresentationModel<ProxyView> {

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime modificationDate;

    private URI name;
    private List<URI> exclusions;
}
