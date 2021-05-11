package io.dataspaceconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

@Data
@NoArgsConstructor
public class AppStoreDesc extends AbstractDescription<AppStore> {

    private URI accessUrl;

    private String title;

    private RegisterStatus registerStatus;

    private List<App> appList;
}
