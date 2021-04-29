package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base class for describing resources.
 * @param <T> The type of the resource.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceDesc<T extends Resource> extends AbstractDescription<T> {

    /**
     * The title of the resource.
     */
    private String title;

    /**
     * The description of the resource.
     */
    private String description;

    /**
     * The keywords of the resource.
     */
    private List<String> keywords;

    /**
     * The publisher of the resource.
     */
    private URI publisher;

    /**
     * The language of the resource.
     */
    private String language;

    /**
     * The licence of the resource.
     */
    private URI licence;

    /**
     * The owner of the resource.
     */
    private URI sovereign;

    /**
     * The endpoint of the resource.
     */
    private URI endpointDocumentation;
}
