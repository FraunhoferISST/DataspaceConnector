TRUNCATE TABLE public.configuration CASCADE;

CREATE TABLE public.resource_samples (
    resource_id uuid NOT NULL,
    samples character varying(2048)
);

ALTER TABLE public.configuration
    RENAME COLUMN connector_endpoint to default_endpoint;

ALTER TABLE public.configuration
    ALTER COLUMN curator TYPE character varying(2048);

ALTER TABLE public.configuration
    ALTER COLUMN default_endpoint TYPE character varying(2048);

ALTER TABLE public.configuration
    ALTER COLUMN maintainer TYPE character varying(2048);

ALTER TABLE public.configuration
    ADD COLUMN connector_id character varying(2048);

ALTER TABLE public.keystore
    ADD COLUMN alias character varying(255);

ALTER TABLE public.resource
    ADD COLUMN payment_modality character varying(255);

ALTER TABLE public.truststore
    ADD COLUMN alias character varying(255);

ALTER TABLE ONLY public.resource_samples
    ADD CONSTRAINT fkrqbwef08s1vqba7f5ank6tmva FOREIGN KEY (resource_id) REFERENCES public.resource(id);
