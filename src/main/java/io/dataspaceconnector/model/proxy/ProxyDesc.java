package io.dataspaceconnector.model.proxy;

import java.net.URI;
import java.util.List;

import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.base.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProxyDesc extends Description {
    private URI          location;
    private List<String> exclusions;
    private AuthenticationDesc authentication;
}
