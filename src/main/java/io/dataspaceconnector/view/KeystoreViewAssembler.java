package io.dataspaceconnector.view;

import io.dataspaceconnector.model.Keystore;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class KeystoreViewAssembler implements
        RepresentationModelAssembler<Keystore, KeystoreView> {
    @Override
    public KeystoreView toModel(final Keystore store) {
        return  new ModelMapper().map(store, KeystoreView.class);
    }
}
