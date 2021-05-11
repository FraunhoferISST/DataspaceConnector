package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ProxyAuthenticationDesc extends AbstractDescription<ProxyAuthentication> {

    private String username;

    private String password;
}
