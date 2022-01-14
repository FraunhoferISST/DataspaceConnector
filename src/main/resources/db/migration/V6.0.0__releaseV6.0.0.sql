ALTER TABLE public.artifact
    ADD COLUMN description character varying(255);

CREATE TABLE public.artifact_subscriptions (
    artifact_id uuid NOT NULL,
    subscriptions_id uuid NOT NULL
);

CREATE TABLE public.broker (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    location character varying(2048),
    status character varying(255)
);

CREATE TABLE public.broker_additional (
    broker_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.broker_offered_resources (
    brokers_id uuid NOT NULL,
    offered_resources_id uuid NOT NULL
);

CREATE TABLE public.configuration (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    active boolean,
    curator character varying(255),
    connector_endpoint character varying(255),
    deploy_mode character varying(255),
    log_level character varying(255),
    maintainer character varying(255),
    outbound_model_version character varying(255),
    security_profile character varying(255),
    status character varying(255),
    version character varying(255),
    keystore_id uuid,
    proxy_id uuid,
    truststore_id uuid
);

CREATE TABLE public.configuration_additional (
    configuration_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.configuration_inbound_model_version (
    configuration_id uuid NOT NULL,
    inbound_model_version character varying(255)
);

ALTER TABLE public.contract
    ADD COLUMN description character varying(255);

ALTER TABLE public.contractrule
    ADD COLUMN description character varying(255);

ALTER TABLE public.data
    DROP COLUMN username;

ALTER TABLE public.data
    DROP COLUMN password;

CREATE TABLE public.data_source_additional (
    data_source_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.datasource (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    type character varying(255),
    authentication_id bigint
);

CREATE TABLE public.endpoint (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    docs character varying(2048),
    info character varying(255),
    location character varying(2048),
    type character varying(255),
    data_source_id uuid
);

CREATE TABLE public.endpoint_additional (
    endpoint_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.keystore (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    location character varying(2048),
    password character varying(255)
);

CREATE TABLE public.keystore_additional (
    keystore_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.proxy (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    location character varying(2048),
    authentication_id bigint
);

CREATE TABLE public.proxy_additional (
    proxy_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.proxy_exclusions (
    proxy_id uuid NOT NULL,
    exclusions character varying(255)
);

ALTER TABLE public.representation
    ADD COLUMN description character varying(255);

CREATE TABLE public.representation_subscriptions (
    representation_id uuid NOT NULL,
    subscriptions_id uuid NOT NULL
);

ALTER TABLE public.resource
    RENAME COLUMN licence TO license;

CREATE TABLE public.resource_subscriptions (
    resource_id uuid NOT NULL,
    subscriptions_id uuid NOT NULL
);

CREATE TABLE public.route (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    configuration text,
    deploy character varying(255),
    end_id uuid,
    start_id uuid
);

CREATE TABLE public.route_additional (
    route_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.route_output (
    route_id uuid NOT NULL,
    output_id uuid NOT NULL
);

CREATE TABLE public.route_steps (
    route_id uuid NOT NULL,
    steps_id uuid NOT NULL
);

CREATE TABLE public.subscription (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    ids_protocol boolean NOT NULL,
    location bytea,
    push_data boolean NOT NULL,
    subscriber bytea,
    target bytea
);

CREATE TABLE public.subscription_additional (
    subscription_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.truststore (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    location character varying(2048),
    password character varying(255)
);

CREATE TABLE public.truststore_additional (
    truststore_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

ALTER TABLE ONLY public.broker_additional
    ADD CONSTRAINT broker_additional_pkey PRIMARY KEY (broker_id, additional_key);

ALTER TABLE ONLY public.broker
    ADD CONSTRAINT broker_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.configuration_additional
    ADD CONSTRAINT configuration_additional_pkey PRIMARY KEY (configuration_id, additional_key);

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.data_source_additional
    ADD CONSTRAINT data_source_additional_pkey PRIMARY KEY (data_source_id, additional_key);

ALTER TABLE ONLY public.datasource
    ADD CONSTRAINT datasource_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.endpoint_additional
    ADD CONSTRAINT endpoint_additional_pkey PRIMARY KEY (endpoint_id, additional_key);

ALTER TABLE ONLY public.endpoint
    ADD CONSTRAINT endpoint_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.keystore_additional
    ADD CONSTRAINT keystore_additional_pkey PRIMARY KEY (keystore_id, additional_key);

ALTER TABLE ONLY public.keystore
    ADD CONSTRAINT keystore_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.proxy_additional
    ADD CONSTRAINT proxy_additional_pkey PRIMARY KEY (proxy_id, additional_key);

ALTER TABLE ONLY public.proxy
    ADD CONSTRAINT proxy_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.route_additional
    ADD CONSTRAINT route_additional_pkey PRIMARY KEY (route_id, additional_key);

ALTER TABLE ONLY public.route
    ADD CONSTRAINT route_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.subscription_additional
    ADD CONSTRAINT subscription_additional_pkey PRIMARY KEY (subscription_id, additional_key);

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT subscription_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.truststore_additional
    ADD CONSTRAINT truststore_additional_pkey PRIMARY KEY (truststore_id, additional_key);

ALTER TABLE ONLY public.truststore
    ADD CONSTRAINT truststore_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.route_steps
    ADD CONSTRAINT uk_2hvysc5saqk4y3qexekw015 UNIQUE (steps_id);

ALTER TABLE ONLY public.artifact_subscriptions
    ADD CONSTRAINT uk_8g340vntvcp4knscsvy1bgopj UNIQUE (subscriptions_id);

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT uk_bj5efn7lht054mm1nfr2rscud UNIQUE (active);

ALTER TABLE ONLY public.representation_subscriptions
    ADD CONSTRAINT uk_dsf5lslfbac5d120rqsprgfqo UNIQUE (subscriptions_id);

ALTER TABLE ONLY public.resource_subscriptions
    ADD CONSTRAINT uk_l81if7mwa7ftd3st366piovp1 UNIQUE (subscriptions_id);

ALTER TABLE ONLY public.route_output
    ADD CONSTRAINT uk_luhfgdmupuurnoxtybwl25jc4 UNIQUE (output_id);

ALTER TABLE ONLY public.route
    ADD CONSTRAINT fk1g3lo2scgxrotyuyiuiptekiq FOREIGN KEY (end_id) REFERENCES public.endpoint(id);

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT fk4h4a0pt25jjihdcuyykh5j0i1 FOREIGN KEY (truststore_id) REFERENCES public.truststore(id);

ALTER TABLE ONLY public.keystore_additional
    ADD CONSTRAINT fk6f9sjbayqd2tmactm5rppom9c FOREIGN KEY (keystore_id) REFERENCES public.keystore(id);

ALTER TABLE ONLY public.configuration_inbound_model_version
    ADD CONSTRAINT fk6llo3rw3vr0e29yxx58smva9d FOREIGN KEY (configuration_id) REFERENCES public.configuration(id);

ALTER TABLE ONLY public.artifact_subscriptions
    ADD CONSTRAINT fk6mfm8q9bn2err5r1fhjjppepp FOREIGN KEY (artifact_id) REFERENCES public.artifact(id);

ALTER TABLE ONLY public.subscription_additional
    ADD CONSTRAINT fk71ubo9u0s73svem5lkcc78r98 FOREIGN KEY (subscription_id) REFERENCES public.subscription(id);

ALTER TABLE ONLY public.proxy_additional
    ADD CONSTRAINT fk74baqqhhlkqcosactxv0rj3x5 FOREIGN KEY (proxy_id) REFERENCES public.proxy(id);

ALTER TABLE ONLY public.route_steps
    ADD CONSTRAINT fk94sxynhfrl9q58pp4b7mj0mf9 FOREIGN KEY (steps_id) REFERENCES public.route(id);

ALTER TABLE ONLY public.datasource
    ADD CONSTRAINT fka61ig1wcffdnldgy6ktghysnt FOREIGN KEY (authentication_id) REFERENCES public.authentication(id);

ALTER TABLE ONLY public.route_output
    ADD CONSTRAINT fkbcdklj53uxgkwbu5gnpa4o4r6 FOREIGN KEY (output_id) REFERENCES public.artifact(id);

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT fkcnuk54rswo29k0oknie4ig87p FOREIGN KEY (proxy_id) REFERENCES public.proxy(id);

ALTER TABLE ONLY public.representation_subscriptions
    ADD CONSTRAINT fkcrvkeo1my0i7btc1hl0vjrp0k FOREIGN KEY (representation_id) REFERENCES public.representation(id);

ALTER TABLE ONLY public.broker_offered_resources
    ADD CONSTRAINT fkdq2d30t4k6sgeprpo8er53au2 FOREIGN KEY (offered_resources_id) REFERENCES public.resource(id);

ALTER TABLE ONLY public.representation_subscriptions
    ADD CONSTRAINT fkejubnajlqcowcxfuy6ddbhl0o FOREIGN KEY (subscriptions_id) REFERENCES public.subscription(id);

ALTER TABLE ONLY public.route_steps
    ADD CONSTRAINT fkf9bb67m7ur0e99ok4dshqk5og FOREIGN KEY (route_id) REFERENCES public.route(id);

ALTER TABLE ONLY public.configuration_additional
    ADD CONSTRAINT fkh81j3cxbwx7bsx5ood605vcjt FOREIGN KEY (configuration_id) REFERENCES public.configuration(id);

ALTER TABLE ONLY public.endpoint
    ADD CONSTRAINT fkhaa3q1mhmewea5d0cek83lf8q FOREIGN KEY (data_source_id) REFERENCES public.datasource(id);

ALTER TABLE ONLY public.endpoint_additional
    ADD CONSTRAINT fkhaseuf282r6ygev7hk5tl30to FOREIGN KEY (endpoint_id) REFERENCES public.endpoint(id);

ALTER TABLE ONLY public.broker_offered_resources
    ADD CONSTRAINT fkjnwllwpuby698lrle9yo0x6s FOREIGN KEY (brokers_id) REFERENCES public.broker(id);

ALTER TABLE ONLY public.proxy
    ADD CONSTRAINT fkjqodc488drfb2mrr31dni7eu4 FOREIGN KEY (authentication_id) REFERENCES public.authentication(id);

ALTER TABLE ONLY public.proxy_exclusions
    ADD CONSTRAINT fkkhj2sdlpcm77dv89nlbaainy6 FOREIGN KEY (proxy_id) REFERENCES public.proxy(id);

ALTER TABLE ONLY public.resource_subscriptions
    ADD CONSTRAINT fkkqdlegy1kfu7so51otnnt8d93 FOREIGN KEY (resource_id) REFERENCES public.resource(id);

ALTER TABLE ONLY public.route_output
    ADD CONSTRAINT fklf1uposbe7ucotdqouphlbpua FOREIGN KEY (route_id) REFERENCES public.route(id);

ALTER TABLE ONLY public.route_additional
    ADD CONSTRAINT fklhkid1suvl4oyk4ccqa88pkcc FOREIGN KEY (route_id) REFERENCES public.route(id);

ALTER TABLE ONLY public.route
    ADD CONSTRAINT fkn2bo3q5fqan770va5dhwel55d FOREIGN KEY (start_id) REFERENCES public.endpoint(id);

ALTER TABLE ONLY public.data_source_additional
    ADD CONSTRAINT fko7xkkayhab49fqu2l43mky4vk FOREIGN KEY (data_source_id) REFERENCES public.datasource(id);

ALTER TABLE ONLY public.resource_subscriptions
    ADD CONSTRAINT fkpbsa263kgj7oqeq5q4o0buig8 FOREIGN KEY (subscriptions_id) REFERENCES public.subscription(id);

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT fkq60ebey7biokeci38he1b4qpn FOREIGN KEY (keystore_id) REFERENCES public.keystore(id);

ALTER TABLE ONLY public.broker_additional
    ADD CONSTRAINT fkqlaqe74yts5ur3vmjvv9o2n2f FOREIGN KEY (broker_id) REFERENCES public.broker(id);

ALTER TABLE ONLY public.truststore_additional
    ADD CONSTRAINT fkqvuo1817a0oh4atlowx0mn61h FOREIGN KEY (truststore_id) REFERENCES public.truststore(id);

ALTER TABLE ONLY public.artifact_subscriptions
    ADD CONSTRAINT fkt0bwn5a7yqtpxq7g2485yo8wu FOREIGN KEY (subscriptions_id) REFERENCES public.subscription(id);
