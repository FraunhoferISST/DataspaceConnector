package io.dataspaceconnector.view;

import io.dataspaceconnector.model.truststore.Truststore;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class TruststoreViewAssembler implements
        RepresentationModelAssembler<Truststore, TruststoreView> {
    @Override
    public TruststoreView toModel(final Truststore store) {
        return new ModelMapper().map(store, TruststoreView.class);
    }
}
