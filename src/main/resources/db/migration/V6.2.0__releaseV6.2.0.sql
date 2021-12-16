CREATE TABLE public.app (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    distribution_service character varying(2048),
    docs character varying(255),
    endpoint_documentation character varying(2048),
    env_variables character varying(255),
    language character varying(255),
    license character varying(2048),
    publisher character varying(2048),
    remote_address character varying(2048),
    remote_id character varying(2048),
    runtime_environment character varying(255),
    sovereign character varying(2048),
    storage_config character varying(255),
    version bigint NOT NULL,
    container_id character varying(255),
    app_store_id uuid,
    data_id bigint
);

CREATE TABLE public.app_additional (
    app_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.app_endpoints (
    app_id uuid NOT NULL,
    endpoints_id uuid NOT NULL
);

CREATE TABLE public.app_keywords (
    app_id uuid NOT NULL,
    keywords character varying(255)
);

CREATE TABLE public.app_store_additional (
    app_store_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.app_supported_policies (
    app_id uuid NOT NULL,
    supported_policies integer
);

CREATE TABLE public.appstore (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    location bytea
);

CREATE TABLE public.appstore_apps (
    app_store_id uuid NOT NULL,
    apps_id uuid NOT NULL
);

ALTER TABLE public.endpoint
    ADD COLUMN endpoint_port integer;

ALTER TABLE public.endpoint
    ADD COLUMN endpoint_type character varying(255);

ALTER TABLE public.endpoint
    ADD COLUMN language character varying(255);

ALTER TABLE public.endpoint
    ADD COLUMN media_type character varying(255);

ALTER TABLE public.endpoint
    ADD COLUMN protocol character varying(255);

ALTER TABLE ONLY public.app_additional
    ADD CONSTRAINT app_additional_pkey PRIMARY KEY (app_id, additional_key);

ALTER TABLE ONLY public.app
    ADD CONSTRAINT app_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.app_store_additional
    ADD CONSTRAINT app_store_additional_pkey PRIMARY KEY (app_store_id, additional_key);

ALTER TABLE ONLY public.appstore
    ADD CONSTRAINT appstore_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.app_endpoints
    ADD CONSTRAINT uk_92agm4wc83hl2d8b2ju1m44e1 UNIQUE (endpoints_id);

ALTER TABLE ONLY public.appstore_apps
    ADD CONSTRAINT uk_jddjmacirf1r4mv8mdb2mxws7 UNIQUE (apps_id);

ALTER TABLE ONLY public.appstore
    ADD CONSTRAINT uk_jq36lxpgxfs4ktn7wrq28wo0f UNIQUE (location);

ALTER TABLE ONLY public.app_endpoints
    ADD CONSTRAINT fk14e6ry1nbaaht8jrnoomp51af FOREIGN KEY (endpoints_id) REFERENCES public.endpoint(id);

ALTER TABLE ONLY public.app
    ADD CONSTRAINT fk15dufq0dmdaablabyi3w0a5on FOREIGN KEY (data_id) REFERENCES public.data(id);

ALTER TABLE ONLY public.app_endpoints
    ADD CONSTRAINT fk5frfuow3in3t1vvqsikdyek6q FOREIGN KEY (app_id) REFERENCES public.app(id);

ALTER TABLE ONLY public.app_additional
    ADD CONSTRAINT fk99lj4jqenphyk8p9clabsbn4n FOREIGN KEY (app_id) REFERENCES public.app(id);

ALTER TABLE ONLY public.app_supported_policies
    ADD CONSTRAINT fkaa5ftvmg6u73e59nlqcxst1s1 FOREIGN KEY (app_id) REFERENCES public.app(id);

ALTER TABLE ONLY public.appstore_apps
    ADD CONSTRAINT fkg5n5yrj2rfuuo6rn5nh6stjej FOREIGN KEY (apps_id) REFERENCES public.app(id);

ALTER TABLE ONLY public.appstore_apps
    ADD CONSTRAINT fkgyl0wchrplmiw0gvtu372my22 FOREIGN KEY (app_store_id) REFERENCES public.appstore(id);

ALTER TABLE ONLY public.app_store_additional
    ADD CONSTRAINT fkh3c21rppm9g3wsbeek216gfx2 FOREIGN KEY (app_store_id) REFERENCES public.appstore(id);

ALTER TABLE ONLY public.app_keywords
    ADD CONSTRAINT fki2vl4xsoexo93tr68fpuonkdo FOREIGN KEY (app_id) REFERENCES public.app(id);

ALTER TABLE ONLY public.app
    ADD CONSTRAINT fkkppvq72vte94spl0jy1j58o92 FOREIGN KEY (app_store_id) REFERENCES public.appstore(id);
