package io.dataspaceconnector.view;

import io.dataspaceconnector.model.proxy.Proxy;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class ProxyViewAssembler  implements
        RepresentationModelAssembler<Proxy, ProxyView> {
    @Override
    public ProxyView toModel(final Proxy proxy) {
        return new ModelMapper().map(proxy, ProxyView.class);
    }
}
