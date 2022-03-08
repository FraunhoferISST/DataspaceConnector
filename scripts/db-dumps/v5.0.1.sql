--
-- Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--    http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 13.4 (Debian 13.4-4.pgdg110+1)
-- Dumped by pg_dump version 14.0 (Ubuntu 14.0-1.pgdg20.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE connectordb;
--
-- Name: connectordb; Type: DATABASE; Schema: -; Owner: connector
--

CREATE DATABASE connectordb WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.utf8';


ALTER DATABASE connectordb OWNER TO connector;

\connect connectordb

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: agreement; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.agreement (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    archived boolean NOT NULL,
    confirmed boolean NOT NULL,
    remote_id bytea,
    value text
);


ALTER TABLE public.agreement OWNER TO connector;

--
-- Name: agreement_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.agreement_additional (
    agreement_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.agreement_additional OWNER TO connector;

--
-- Name: agreement_artifacts; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.agreement_artifacts (
    agreements_id uuid NOT NULL,
    artifacts_id uuid NOT NULL
);


ALTER TABLE public.agreement_artifacts OWNER TO connector;

--
-- Name: artifact; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.artifact (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
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


ALTER TABLE public.artifact OWNER TO connector;

--
-- Name: artifact_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.artifact_additional (
    artifact_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.artifact_additional OWNER TO connector;

--
-- Name: catalog; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.catalog (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255)
);


ALTER TABLE public.catalog OWNER TO connector;

--
-- Name: catalog_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.catalog_additional (
    catalog_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.catalog_additional OWNER TO connector;

--
-- Name: catalog_offered_resources; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.catalog_offered_resources (
    catalogs_id uuid NOT NULL,
    offered_resources_id uuid NOT NULL
);


ALTER TABLE public.catalog_offered_resources OWNER TO connector;

--
-- Name: catalog_requested_resources; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.catalog_requested_resources (
    catalogs_id uuid NOT NULL,
    requested_resources_id uuid NOT NULL
);


ALTER TABLE public.catalog_requested_resources OWNER TO connector;

--
-- Name: contract; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.contract (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    consumer bytea,
    contract_end timestamp without time zone,
    provider bytea,
    remote_id bytea,
    contract_start timestamp without time zone,
    title character varying(255)
);


ALTER TABLE public.contract OWNER TO connector;

--
-- Name: contract_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.contract_additional (
    contract_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.contract_additional OWNER TO connector;

--
-- Name: contract_rule_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.contract_rule_additional (
    contract_rule_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.contract_rule_additional OWNER TO connector;

--
-- Name: contract_rules; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.contract_rules (
    contracts_id uuid NOT NULL,
    rules_id uuid NOT NULL
);


ALTER TABLE public.contract_rules OWNER TO connector;

--
-- Name: contractrule; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.contractrule (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    remote_id bytea,
    title character varying(255),
    value text
);


ALTER TABLE public.contractrule OWNER TO connector;

--
-- Name: data; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.data (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    deleted boolean,
    access_url character varying(255),
    password character varying(255),
    username character varying(255),
    value oid
);


ALTER TABLE public.data OWNER TO connector;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: connector
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO connector;

--
-- Name: representation; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.representation (
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    language character varying(255),
    media_type character varying(255),
    remote_id bytea,
    standard character varying(255),
    title character varying(255)
);


ALTER TABLE public.representation OWNER TO connector;

--
-- Name: representation_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.representation_additional (
    representation_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.representation_additional OWNER TO connector;

--
-- Name: representation_artifacts; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.representation_artifacts (
    representations_id uuid NOT NULL,
    artifacts_id uuid NOT NULL
);


ALTER TABLE public.representation_artifacts OWNER TO connector;

--
-- Name: resource; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
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


ALTER TABLE public.resource OWNER TO connector;

--
-- Name: resource_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource_additional (
    resource_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.resource_additional OWNER TO connector;

--
-- Name: resource_contracts; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource_contracts (
    resources_id uuid NOT NULL,
    contracts_id uuid NOT NULL
);


ALTER TABLE public.resource_contracts OWNER TO connector;

--
-- Name: resource_keywords; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource_keywords (
    resource_id uuid NOT NULL,
    keywords character varying(255)
);


ALTER TABLE public.resource_keywords OWNER TO connector;

--
-- Name: resource_representations; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource_representations (
    resources_id uuid NOT NULL,
    representations_id uuid NOT NULL
);


ALTER TABLE public.resource_representations OWNER TO connector;

--
-- Data for Name: agreement; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.agreement (id, created_date, deleted, modified_date, archived, confirmed, remote_id, value) FROM stdin;
\.


--
-- Data for Name: agreement_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.agreement_additional (agreement_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: agreement_artifacts; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.agreement_artifacts (agreements_id, artifacts_id) FROM stdin;
\.


--
-- Data for Name: artifact; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.artifact (dtype, id, created_date, deleted, modified_date, automated_download, byte_size, check_sum, num_accessed, remote_address, remote_id, title, data_id) FROM stdin;
\.


--
-- Data for Name: artifact_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.artifact_additional (artifact_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: catalog; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.catalog (id, created_date, deleted, modified_date, description, title) FROM stdin;
\.


--
-- Data for Name: catalog_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.catalog_additional (catalog_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: catalog_offered_resources; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.catalog_offered_resources (catalogs_id, offered_resources_id) FROM stdin;
\.


--
-- Data for Name: catalog_requested_resources; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.catalog_requested_resources (catalogs_id, requested_resources_id) FROM stdin;
\.


--
-- Data for Name: contract; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.contract (id, created_date, deleted, modified_date, consumer, contract_end, provider, remote_id, contract_start, title) FROM stdin;
\.


--
-- Data for Name: contract_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.contract_additional (contract_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: contract_rule_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.contract_rule_additional (contract_rule_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: contract_rules; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.contract_rules (contracts_id, rules_id) FROM stdin;
\.


--
-- Data for Name: contractrule; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.contractrule (id, created_date, deleted, modified_date, remote_id, title, value) FROM stdin;
\.


--
-- Data for Name: data; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.data (dtype, id, deleted, access_url, password, username, value) FROM stdin;
\.


--
-- Data for Name: representation; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.representation (id, created_date, deleted, modified_date, language, media_type, remote_id, standard, title) FROM stdin;
\.


--
-- Data for Name: representation_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.representation_additional (representation_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: representation_artifacts; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.representation_artifacts (representations_id, artifacts_id) FROM stdin;
\.


--
-- Data for Name: resource; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource (dtype, id, created_date, deleted, modified_date, description, endpoint_documentation, language, licence, publisher, sovereign, title, version, remote_id) FROM stdin;
\.


--
-- Data for Name: resource_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource_additional (resource_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: resource_contracts; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource_contracts (resources_id, contracts_id) FROM stdin;
\.


--
-- Data for Name: resource_keywords; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource_keywords (resource_id, keywords) FROM stdin;
\.


--
-- Data for Name: resource_representations; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource_representations (resources_id, representations_id) FROM stdin;
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: connector
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);


--
-- Name: agreement_additional agreement_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.agreement_additional
    ADD CONSTRAINT agreement_additional_pkey PRIMARY KEY (agreement_id, additional_key);


--
-- Name: agreement agreement_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.agreement
    ADD CONSTRAINT agreement_pkey PRIMARY KEY (id);


--
-- Name: artifact_additional artifact_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact_additional
    ADD CONSTRAINT artifact_additional_pkey PRIMARY KEY (artifact_id, additional_key);


--
-- Name: artifact artifact_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact
    ADD CONSTRAINT artifact_pkey PRIMARY KEY (id);


--
-- Name: catalog_additional catalog_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_additional
    ADD CONSTRAINT catalog_additional_pkey PRIMARY KEY (catalog_id, additional_key);


--
-- Name: catalog catalog_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog
    ADD CONSTRAINT catalog_pkey PRIMARY KEY (id);


--
-- Name: contract_additional contract_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract_additional
    ADD CONSTRAINT contract_additional_pkey PRIMARY KEY (contract_id, additional_key);


--
-- Name: contract contract_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_pkey PRIMARY KEY (id);


--
-- Name: contract_rule_additional contract_rule_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract_rule_additional
    ADD CONSTRAINT contract_rule_additional_pkey PRIMARY KEY (contract_rule_id, additional_key);


--
-- Name: contractrule contractrule_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contractrule
    ADD CONSTRAINT contractrule_pkey PRIMARY KEY (id);


--
-- Name: data data_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.data
    ADD CONSTRAINT data_pkey PRIMARY KEY (id);


--
-- Name: representation_additional representation_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation_additional
    ADD CONSTRAINT representation_additional_pkey PRIMARY KEY (representation_id, additional_key);


--
-- Name: representation representation_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation
    ADD CONSTRAINT representation_pkey PRIMARY KEY (id);


--
-- Name: resource_additional resource_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_additional
    ADD CONSTRAINT resource_additional_pkey PRIMARY KEY (resource_id, additional_key);


--
-- Name: resource resource_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource
    ADD CONSTRAINT resource_pkey PRIMARY KEY (id);


--
-- Name: artifact_additional fk39nygv2yn0osg687e1a42njrv; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact_additional
    ADD CONSTRAINT fk39nygv2yn0osg687e1a42njrv FOREIGN KEY (artifact_id) REFERENCES public.artifact(id);


--
-- Name: representation_artifacts fk4spj9uchowrwqldiua7xerffb; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation_artifacts
    ADD CONSTRAINT fk4spj9uchowrwqldiua7xerffb FOREIGN KEY (artifacts_id) REFERENCES public.artifact(id);


--
-- Name: resource_contracts fk5asnjcfur742nl713f8lcrbib; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_contracts
    ADD CONSTRAINT fk5asnjcfur742nl713f8lcrbib FOREIGN KEY (contracts_id) REFERENCES public.contract(id);


--
-- Name: agreement_artifacts fk7mjl82ejj66tmmrkspyoyailp; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.agreement_artifacts
    ADD CONSTRAINT fk7mjl82ejj66tmmrkspyoyailp FOREIGN KEY (agreements_id) REFERENCES public.agreement(id);


--
-- Name: catalog_offered_resources fk98lr0lvbouf53nx6e2m51p36f; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_offered_resources
    ADD CONSTRAINT fk98lr0lvbouf53nx6e2m51p36f FOREIGN KEY (catalogs_id) REFERENCES public.catalog(id);


--
-- Name: contract_rules fka7kvb987lhl2yi5m2ayrgr6kk; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract_rules
    ADD CONSTRAINT fka7kvb987lhl2yi5m2ayrgr6kk FOREIGN KEY (rules_id) REFERENCES public.contractrule(id);


--
-- Name: catalog_offered_resources fkbisuxjltrepmrb0evdctb16xl; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_offered_resources
    ADD CONSTRAINT fkbisuxjltrepmrb0evdctb16xl FOREIGN KEY (offered_resources_id) REFERENCES public.resource(id);


--
-- Name: contract_rules fkc13xssp8ajsjnxvrv6umejwt8; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract_rules
    ADD CONSTRAINT fkc13xssp8ajsjnxvrv6umejwt8 FOREIGN KEY (contracts_id) REFERENCES public.contract(id);


--
-- Name: artifact fkcjyuh4gd12p2clxcdrywngnrk; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact
    ADD CONSTRAINT fkcjyuh4gd12p2clxcdrywngnrk FOREIGN KEY (data_id) REFERENCES public.data(id);


--
-- Name: representation_additional fkdcwg26vn7iksqwt3j0uv8bhsh; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation_additional
    ADD CONSTRAINT fkdcwg26vn7iksqwt3j0uv8bhsh FOREIGN KEY (representation_id) REFERENCES public.representation(id);


--
-- Name: resource_representations fkdgkh2o4ihome47k0cv9ei28uw; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_representations
    ADD CONSTRAINT fkdgkh2o4ihome47k0cv9ei28uw FOREIGN KEY (representations_id) REFERENCES public.representation(id);


--
-- Name: catalog_additional fkf1tu8lci4kjy9b1mw5wnxht0g; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_additional
    ADD CONSTRAINT fkf1tu8lci4kjy9b1mw5wnxht0g FOREIGN KEY (catalog_id) REFERENCES public.catalog(id);


--
-- Name: agreement_additional fkfhfyofmftgys2ssia8n548xcl; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.agreement_additional
    ADD CONSTRAINT fkfhfyofmftgys2ssia8n548xcl FOREIGN KEY (agreement_id) REFERENCES public.agreement(id);


--
-- Name: contract_rule_additional fkh7xh0ik3ls87p4kvacl9q2med; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract_rule_additional
    ADD CONSTRAINT fkh7xh0ik3ls87p4kvacl9q2med FOREIGN KEY (contract_rule_id) REFERENCES public.contractrule(id);


--
-- Name: resource_additional fkii3qb000uovadjpmgnyk4i2oy; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_additional
    ADD CONSTRAINT fkii3qb000uovadjpmgnyk4i2oy FOREIGN KEY (resource_id) REFERENCES public.resource(id);


--
-- Name: agreement_artifacts fkk81vbye4esds4rdyhna320vt7; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.agreement_artifacts
    ADD CONSTRAINT fkk81vbye4esds4rdyhna320vt7 FOREIGN KEY (artifacts_id) REFERENCES public.artifact(id);


--
-- Name: resource_contracts fkll9wg6x5ir9vtnmat64d48icj; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_contracts
    ADD CONSTRAINT fkll9wg6x5ir9vtnmat64d48icj FOREIGN KEY (resources_id) REFERENCES public.resource(id);


--
-- Name: resource_keywords fklpel7ma6v39pj4t3e5igou7s8; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_keywords
    ADD CONSTRAINT fklpel7ma6v39pj4t3e5igou7s8 FOREIGN KEY (resource_id) REFERENCES public.resource(id);


--
-- Name: representation_artifacts fkm2tgxey1y95fmavx8scq9ym8o; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation_artifacts
    ADD CONSTRAINT fkm2tgxey1y95fmavx8scq9ym8o FOREIGN KEY (representations_id) REFERENCES public.representation(id);


--
-- Name: contract_additional fkmvk084orjax8cjhy3fdh1ij1m; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract_additional
    ADD CONSTRAINT fkmvk084orjax8cjhy3fdh1ij1m FOREIGN KEY (contract_id) REFERENCES public.contract(id);


--
-- Name: catalog_requested_resources fksavyh2um62h3ueex93q8xf8xj; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_requested_resources
    ADD CONSTRAINT fksavyh2um62h3ueex93q8xf8xj FOREIGN KEY (catalogs_id) REFERENCES public.catalog(id);


--
-- Name: catalog_requested_resources fksy60o45qscp2gylf33e21xyy8; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_requested_resources
    ADD CONSTRAINT fksy60o45qscp2gylf33e21xyy8 FOREIGN KEY (requested_resources_id) REFERENCES public.resource(id);


--
-- Name: resource_representations fktntca482f3j16nwbrb9y6y51l; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_representations
    ADD CONSTRAINT fktntca482f3j16nwbrb9y6y51l FOREIGN KEY (resources_id) REFERENCES public.resource(id);


--
-- PostgreSQL database dump complete
--
