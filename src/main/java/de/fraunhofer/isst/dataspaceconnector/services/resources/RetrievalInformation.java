package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.net.URI;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/**
 * Bundles information for retrieving data from another connector.
 */
@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class RetrievalInformation {
    /**
     * The transferContract with which the data transfer is authorized.
     */
    private @NonNull URI transferContract;

    /**
     * If the data should be downloaded.
     * null  - Let the connector decide.
     * true  - Always download.
     * false - Do not download the data under any condition.
     */
    private Boolean forceDownload;

    /**
     * Query option for limiting the scope of the data pulled.
     * The query input may be ignored by some connectors.
     */
    private QueryInput queryInput;
}
