CREATE TABLE public.agreement (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    archived boolean NOT NULL,
    confirmed boolean NOT NULL,
    remote_id bytea,
    value text
);

CREATE TABLE public.agreement_additional (
    agreement_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.agreement_artifacts (
    agreements_id uuid NOT NULL,
    artifacts_id uuid NOT NULL
);

CREATE TABLE public.artifact (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    automated_download boolean NOT NULL,
    byte_size bigint NOT NULL,
    check_sum bigint NOT NULL,
    num_accessed bigint NOT NULL,
    remote_address bytea,
    remote_id bytea,
    title character varying(255),
    data_id bigint
);

CREATE TABLE public.artifact_additional (
    artifact_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.catalog (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255)
);

CREATE TABLE public.catalog_additional (
    catalog_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.catalog_offered_resources (
    catalogs_id uuid NOT NULL,
    offered_resources_id uuid NOT NULL
);

CREATE TABLE public.catalog_requested_resources (
    catalogs_id uuid NOT NULL,
    requested_resources_id uuid NOT NULL
);

CREATE TABLE public.contract (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    consumer bytea,
    contract_end timestamp without time zone,
    provider bytea,
    remote_id bytea,
    contract_start timestamp without time zone,
    title character varying(255)
);

CREATE TABLE public.contract_additional (
    contract_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.contract_rule_additional (
    contract_rule_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.contract_rules (
    contracts_id uuid NOT NULL,
    rules_id uuid NOT NULL
);

CREATE TABLE public.contractrule (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    remote_id bytea,
    title character varying(255),
    value text
);

CREATE TABLE public.data (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    deleted boolean DEFAULT false,
    value oid,
    access_url character varying(255),
    password character varying(255),
    username character varying(255)
);

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;



CREATE TABLE public.representation (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    language character varying(255),
    media_type character varying(255),
    remote_id bytea,
    standard character varying(255),
    title character varying(255)
);

CREATE TABLE public.representation_additional (
    representation_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.representation_artifacts (
    representations_id uuid NOT NULL,
    artifacts_id uuid NOT NULL
);

CREATE TABLE public.resource (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean DEFAULT false,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    endpoint_documentation bytea,
    language character varying(255),
    licence bytea,
    publisher bytea,
    sovereign bytea,
    title character varying(255),
    version bigint NOT NULL,
    remote_id bytea
);

CREATE TABLE public.resource_additional (
    resource_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);

CREATE TABLE public.resource_contracts (
    resources_id uuid NOT NULL,
    contracts_id uuid NOT NULL
);

CREATE TABLE public.resource_keywords (
    resource_id uuid NOT NULL,
    keywords character varying(255)
);

CREATE TABLE public.resource_representations (
    resources_id uuid NOT NULL,
    representations_id uuid NOT NULL
);

ALTER TABLE ONLY public.agreement_additional
    ADD CONSTRAINT agreement_additional_pkey PRIMARY KEY (agreement_id, additional_key);

ALTER TABLE ONLY public.agreement
    ADD CONSTRAINT agreement_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.artifact_additional
    ADD CONSTRAINT artifact_additional_pkey PRIMARY KEY (artifact_id, additional_key);

ALTER TABLE ONLY public.artifact
    ADD CONSTRAINT artifact_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.catalog_additional
    ADD CONSTRAINT catalog_additional_pkey PRIMARY KEY (catalog_id, additional_key);

ALTER TABLE ONLY public.catalog
    ADD CONSTRAINT catalog_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.contract_additional
    ADD CONSTRAINT contract_additional_pkey PRIMARY KEY (contract_id, additional_key);

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.contract_rule_additional
    ADD CONSTRAINT contract_rule_additional_pkey PRIMARY KEY (contract_rule_id, additional_key);

ALTER TABLE ONLY public.contractrule
    ADD CONSTRAINT contractrule_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.data
    ADD CONSTRAINT data_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.representation_additional
    ADD CONSTRAINT representation_additional_pkey PRIMARY KEY (representation_id, additional_key);

ALTER TABLE ONLY public.representation
    ADD CONSTRAINT representation_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.resource_additional
    ADD CONSTRAINT resource_additional_pkey PRIMARY KEY (resource_id, additional_key);

ALTER TABLE ONLY public.resource
    ADD CONSTRAINT resource_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.artifact_additional
    ADD CONSTRAINT fk39nygv2yn0osg687e1a42njrv FOREIGN KEY (artifact_id) REFERENCES public.artifact(id);

ALTER TABLE ONLY public.representation_artifacts
    ADD CONSTRAINT fk4spj9uchowrwqldiua7xerffb FOREIGN KEY (artifacts_id) REFERENCES public.artifact(id);

ALTER TABLE ONLY public.resource_contracts
    ADD CONSTRAINT fk5asnjcfur742nl713f8lcrbib FOREIGN KEY (contracts_id) REFERENCES public.contract(id);

ALTER TABLE ONLY public.agreement_artifacts
    ADD CONSTRAINT fk7mjl82ejj66tmmrkspyoyailp FOREIGN KEY (agreements_id) REFERENCES public.agreement(id);

ALTER TABLE ONLY public.catalog_offered_resources
    ADD CONSTRAINT fk98lr0lvbouf53nx6e2m51p36f FOREIGN KEY (catalogs_id) REFERENCES public.catalog(id);

ALTER TABLE ONLY public.contract_rules
    ADD CONSTRAINT fka7kvb987lhl2yi5m2ayrgr6kk FOREIGN KEY (rules_id) REFERENCES public.contractrule(id);

ALTER TABLE ONLY public.catalog_offered_resources
    ADD CONSTRAINT fkbisuxjltrepmrb0evdctb16xl FOREIGN KEY (offered_resources_id) REFERENCES public.resource(id);

ALTER TABLE ONLY public.contract_rules
    ADD CONSTRAINT fkc13xssp8ajsjnxvrv6umejwt8 FOREIGN KEY (contracts_id) REFERENCES public.contract(id);

ALTER TABLE ONLY public.artifact
    ADD CONSTRAINT fkcjyuh4gd12p2clxcdrywngnrk FOREIGN KEY (data_id) REFERENCES public.data(id);

ALTER TABLE ONLY public.representation_additional
    ADD CONSTRAINT fkdcwg26vn7iksqwt3j0uv8bhsh FOREIGN KEY (representation_id) REFERENCES public.representation(id);

ALTER TABLE ONLY public.resource_representations
    ADD CONSTRAINT fkdgkh2o4ihome47k0cv9ei28uw FOREIGN KEY (representations_id) REFERENCES public.representation(id);

ALTER TABLE ONLY public.catalog_additional
    ADD CONSTRAINT fkf1tu8lci4kjy9b1mw5wnxht0g FOREIGN KEY (catalog_id) REFERENCES public.catalog(id);

ALTER TABLE ONLY public.agreement_additional
    ADD CONSTRAINT fkfhfyofmftgys2ssia8n548xcl FOREIGN KEY (agreement_id) REFERENCES public.agreement(id);

ALTER TABLE ONLY public.contract_rule_additional
    ADD CONSTRAINT fkh7xh0ik3ls87p4kvacl9q2med FOREIGN KEY (contract_rule_id) REFERENCES public.contractrule(id);

ALTER TABLE ONLY public.resource_additional
    ADD CONSTRAINT fkii3qb000uovadjpmgnyk4i2oy FOREIGN KEY (resource_id) REFERENCES public.resource(id);

ALTER TABLE ONLY public.agreement_artifacts
    ADD CONSTRAINT fkk81vbye4esds4rdyhna320vt7 FOREIGN KEY (artifacts_id) REFERENCES public.artifact(id);

ALTER TABLE ONLY public.resource_contracts
    ADD CONSTRAINT fkll9wg6x5ir9vtnmat64d48icj FOREIGN KEY (resources_id) REFERENCES public.resource(id);

ALTER TABLE ONLY public.resource_keywords
    ADD CONSTRAINT fklpel7ma6v39pj4t3e5igou7s8 FOREIGN KEY (resource_id) REFERENCES public.resource(id);

ALTER TABLE ONLY public.representation_artifacts
    ADD CONSTRAINT fkm2tgxey1y95fmavx8scq9ym8o FOREIGN KEY (representations_id) REFERENCES public.representation(id);

ALTER TABLE ONLY public.contract_additional
    ADD CONSTRAINT fkmvk084orjax8cjhy3fdh1ij1m FOREIGN KEY (contract_id) REFERENCES public.contract(id);

ALTER TABLE ONLY public.catalog_requested_resources
    ADD CONSTRAINT fksavyh2um62h3ueex93q8xf8xj FOREIGN KEY (catalogs_id) REFERENCES public.catalog(id);

ALTER TABLE ONLY public.catalog_requested_resources
    ADD CONSTRAINT fksy60o45qscp2gylf33e21xyy8 FOREIGN KEY (requested_resources_id) REFERENCES public.resource(id);

ALTER TABLE ONLY public.resource_representations
    ADD CONSTRAINT fktntca482f3j16nwbrb9y6y51l FOREIGN KEY (resources_id) REFERENCES public.resource(id);
