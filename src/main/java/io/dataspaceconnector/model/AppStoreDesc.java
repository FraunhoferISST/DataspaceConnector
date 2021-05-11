package io.dataspaceconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

/**
 * Describes a app store's properties.
 */
@Data
@NoArgsConstructor
public class AppStoreDesc extends AbstractDescription<AppStore> {

    /**
     * The access url of the app store.
     */
    private URI accessUrl;

    /**
     * The title of the app store.
     */
    private String title;

    /**
     * The registration status.
     */
    private RegisterStatus registerStatus;

    /**
     * The list of apps.
     */
    private List<App> appList;
}
