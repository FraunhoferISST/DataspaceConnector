package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class AuthenticationDesc extends AbstractDescription<Authentication> {

    private String username;

    private String password;
}
