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
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    archived boolean NOT NULL,
    confirmed boolean NOT NULL,
    remote_id character varying(2048),
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
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    automated_download boolean NOT NULL,
    byte_size bigint NOT NULL,
    check_sum bigint NOT NULL,
    num_accessed bigint NOT NULL,
    remote_address character varying(2048),
    remote_id character varying(2048),
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
-- Name: artifact_subscriptions; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.artifact_subscriptions (
    artifact_id uuid NOT NULL,
    subscriptions_id uuid NOT NULL
);


ALTER TABLE public.artifact_subscriptions OWNER TO connector;

--
-- Name: authentication; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.authentication (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    deleted boolean,
    key character varying(255),
    value character varying(255),
    password character varying(255),
    username character varying(255)
);


ALTER TABLE public.authentication OWNER TO connector;

--
-- Name: broker; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.broker (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    location character varying(2048),
    status character varying(255)
);


ALTER TABLE public.broker OWNER TO connector;

--
-- Name: broker_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.broker_additional (
    broker_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.broker_additional OWNER TO connector;

--
-- Name: broker_offered_resources; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.broker_offered_resources (
    brokers_id uuid NOT NULL,
    offered_resources_id uuid NOT NULL
);


ALTER TABLE public.broker_offered_resources OWNER TO connector;

--
-- Name: catalog; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.catalog (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
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
-- Name: configuration; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.configuration (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    active boolean,
    connector_id character varying(2048),
    curator character varying(2048),
    default_endpoint character varying(2048),
    deploy_mode character varying(255),
    log_level character varying(255),
    maintainer character varying(2048),
    outbound_model_version character varying(255),
    security_profile character varying(255),
    status character varying(255),
    version character varying(255),
    keystore_id uuid,
    proxy_id uuid,
    truststore_id uuid
);


ALTER TABLE public.configuration OWNER TO connector;

--
-- Name: configuration_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.configuration_additional (
    configuration_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.configuration_additional OWNER TO connector;

--
-- Name: configuration_inbound_model_version; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.configuration_inbound_model_version (
    configuration_id uuid NOT NULL,
    inbound_model_version character varying(255)
);


ALTER TABLE public.configuration_inbound_model_version OWNER TO connector;

--
-- Name: contract; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.contract (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    consumer character varying(2048),
    contract_end timestamp without time zone,
    provider character varying(2048),
    remote_id character varying(2048),
    contract_start timestamp without time zone
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
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    remote_id character varying(2048),
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
    value oid,
    access_url character varying(2048)
);


ALTER TABLE public.data OWNER TO connector;

--
-- Name: data_authentication; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.data_authentication (
    remote_data_id bigint NOT NULL,
    authentication_id bigint NOT NULL
);


ALTER TABLE public.data_authentication OWNER TO connector;

--
-- Name: data_source_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.data_source_additional (
    data_source_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.data_source_additional OWNER TO connector;

--
-- Name: datasource; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.datasource (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    type character varying(255),
    authentication_id bigint
);


ALTER TABLE public.datasource OWNER TO connector;

--
-- Name: endpoint; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.endpoint (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    docs character varying(2048),
    info character varying(255),
    location character varying(2048),
    type character varying(255),
    data_source_id uuid
);


ALTER TABLE public.endpoint OWNER TO connector;

--
-- Name: endpoint_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.endpoint_additional (
    endpoint_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.endpoint_additional OWNER TO connector;

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
-- Name: keystore; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.keystore (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    alias character varying(255),
    location character varying(2048),
    password character varying(255)
);


ALTER TABLE public.keystore OWNER TO connector;

--
-- Name: keystore_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.keystore_additional (
    keystore_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.keystore_additional OWNER TO connector;

--
-- Name: proxy; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.proxy (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    location character varying(2048),
    authentication_id bigint
);


ALTER TABLE public.proxy OWNER TO connector;

--
-- Name: proxy_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.proxy_additional (
    proxy_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.proxy_additional OWNER TO connector;

--
-- Name: proxy_exclusions; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.proxy_exclusions (
    proxy_id uuid NOT NULL,
    exclusions character varying(255)
);


ALTER TABLE public.proxy_exclusions OWNER TO connector;

--
-- Name: representation; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.representation (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    language character varying(255),
    media_type character varying(255),
    remote_id character varying(2048),
    standard character varying(255)
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
-- Name: representation_subscriptions; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.representation_subscriptions (
    representation_id uuid NOT NULL,
    subscriptions_id uuid NOT NULL
);


ALTER TABLE public.representation_subscriptions OWNER TO connector;

--
-- Name: resource; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource (
    dtype character varying(31) NOT NULL,
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    endpoint_documentation character varying(2048),
    language character varying(255),
    license character varying(2048),
    payment_modality character varying(255),
    publisher character varying(2048),
    sovereign character varying(2048),
    version bigint NOT NULL,
    remote_id character varying(2048)
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
-- Name: resource_samples; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource_samples (
    resource_id uuid NOT NULL,
    samples character varying(2048)
);


ALTER TABLE public.resource_samples OWNER TO connector;

--
-- Name: resource_subscriptions; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.resource_subscriptions (
    resource_id uuid NOT NULL,
    subscriptions_id uuid NOT NULL
);


ALTER TABLE public.resource_subscriptions OWNER TO connector;

--
-- Name: route; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.route (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    configuration text,
    deploy character varying(255),
    end_id uuid,
    start_id uuid
);


ALTER TABLE public.route OWNER TO connector;

--
-- Name: route_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.route_additional (
    route_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.route_additional OWNER TO connector;

--
-- Name: route_output; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.route_output (
    route_id uuid NOT NULL,
    output_id uuid NOT NULL
);


ALTER TABLE public.route_output OWNER TO connector;

--
-- Name: route_steps; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.route_steps (
    route_id uuid NOT NULL,
    steps_id uuid NOT NULL
);


ALTER TABLE public.route_steps OWNER TO connector;

--
-- Name: subscription; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.subscription (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    description character varying(255),
    title character varying(255),
    ids_protocol boolean NOT NULL,
    location character varying(2048),
    push_data boolean NOT NULL,
    subscriber character varying(2048),
    target character varying(2048)
);


ALTER TABLE public.subscription OWNER TO connector;

--
-- Name: subscription_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.subscription_additional (
    subscription_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.subscription_additional OWNER TO connector;

--
-- Name: truststore; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.truststore (
    id uuid NOT NULL,
    bootstrap_id character varying(2048),
    created_date timestamp without time zone NOT NULL,
    deleted boolean,
    modified_date timestamp without time zone NOT NULL,
    alias character varying(255),
    location character varying(2048),
    password character varying(255)
);


ALTER TABLE public.truststore OWNER TO connector;

--
-- Name: truststore_additional; Type: TABLE; Schema: public; Owner: connector
--

CREATE TABLE public.truststore_additional (
    truststore_id uuid NOT NULL,
    additional character varying(255),
    additional_key character varying(255) NOT NULL
);


ALTER TABLE public.truststore_additional OWNER TO connector;

--
-- Data for Name: agreement; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.agreement (id, bootstrap_id, created_date, deleted, modified_date, archived, confirmed, remote_id, value) FROM stdin;
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

COPY public.artifact (dtype, id, bootstrap_id, created_date, deleted, modified_date, description, title, automated_download, byte_size, check_sum, num_accessed, remote_address, remote_id, data_id) FROM stdin;
\.


--
-- Data for Name: artifact_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.artifact_additional (artifact_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: artifact_subscriptions; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.artifact_subscriptions (artifact_id, subscriptions_id) FROM stdin;
\.


--
-- Data for Name: authentication; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.authentication (dtype, id, deleted, key, value, password, username) FROM stdin;
\.


--
-- Data for Name: broker; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.broker (id, bootstrap_id, created_date, deleted, modified_date, description, title, location, status) FROM stdin;
\.


--
-- Data for Name: broker_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.broker_additional (broker_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: broker_offered_resources; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.broker_offered_resources (brokers_id, offered_resources_id) FROM stdin;
\.


--
-- Data for Name: catalog; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.catalog (id, bootstrap_id, created_date, deleted, modified_date, description, title) FROM stdin;
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
-- Data for Name: configuration; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.configuration (id, bootstrap_id, created_date, deleted, modified_date, description, title, active, connector_id, curator, default_endpoint, deploy_mode, log_level, maintainer, outbound_model_version, security_profile, status, version, keystore_id, proxy_id, truststore_id) FROM stdin;
1a0a2e2f-d736-4477-980e-919d0cba8dfa	\N	2021-10-08 11:52:53.381126	f	2021-10-08 11:52:53.381145	IDS Connector with static example resources hosted by the Fraunhofer ISST	Dataspace Connector	t	https://localhost:8080	https://www.isst.fraunhofer.de/	https://localhost:8080/api/ids/data	TEST	WARN	https://www.isst.fraunhofer.de/	4.1.2	BASE_SECURITY	ONLINE	6.1.1	4f36cc0b-15ef-4049-ad1f-8ea9dcf4324f	\N	4ddfccf4-ea8a-49e4-8b53-d963a563cf3a
\.


--
-- Data for Name: configuration_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.configuration_additional (configuration_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: configuration_inbound_model_version; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.configuration_inbound_model_version (configuration_id, inbound_model_version) FROM stdin;
1a0a2e2f-d736-4477-980e-919d0cba8dfa	4.1.2
1a0a2e2f-d736-4477-980e-919d0cba8dfa	4.0.0
1a0a2e2f-d736-4477-980e-919d0cba8dfa	4.1.0
\.


--
-- Data for Name: contract; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.contract (id, bootstrap_id, created_date, deleted, modified_date, description, title, consumer, contract_end, provider, remote_id, contract_start) FROM stdin;
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

COPY public.contractrule (id, bootstrap_id, created_date, deleted, modified_date, description, title, remote_id, value) FROM stdin;
\.


--
-- Data for Name: data; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.data (dtype, id, deleted, value, access_url) FROM stdin;
\.


--
-- Data for Name: data_authentication; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.data_authentication (remote_data_id, authentication_id) FROM stdin;
\.


--
-- Data for Name: data_source_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.data_source_additional (data_source_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: datasource; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.datasource (id, bootstrap_id, created_date, deleted, modified_date, type, authentication_id) FROM stdin;
\.


--
-- Data for Name: endpoint; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.endpoint (dtype, id, bootstrap_id, created_date, deleted, modified_date, docs, info, location, type, data_source_id) FROM stdin;
\.


--
-- Data for Name: endpoint_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.endpoint_additional (endpoint_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: keystore; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.keystore (id, bootstrap_id, created_date, deleted, modified_date, alias, location, password) FROM stdin;
4f36cc0b-15ef-4049-ad1f-8ea9dcf4324f	\N	2021-10-08 11:52:53.370776	f	2021-10-08 11:52:53.370824	1	file:///conf/keystore-localhost.p12
\.


--
-- Data for Name: keystore_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.keystore_additional (keystore_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: proxy; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.proxy (id, bootstrap_id, created_date, deleted, modified_date, location, authentication_id) FROM stdin;
\.


--
-- Data for Name: proxy_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.proxy_additional (proxy_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: proxy_exclusions; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.proxy_exclusions (proxy_id, exclusions) FROM stdin;
\.


--
-- Data for Name: representation; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.representation (id, bootstrap_id, created_date, deleted, modified_date, description, title, language, media_type, remote_id, standard) FROM stdin;
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
-- Data for Name: representation_subscriptions; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.representation_subscriptions (representation_id, subscriptions_id) FROM stdin;
\.


--
-- Data for Name: resource; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource (dtype, id, bootstrap_id, created_date, deleted, modified_date, description, title, endpoint_documentation, language, license, payment_modality, publisher, sovereign, version, remote_id) FROM stdin;
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
-- Data for Name: resource_samples; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource_samples (resource_id, samples) FROM stdin;
\.


--
-- Data for Name: resource_subscriptions; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.resource_subscriptions (resource_id, subscriptions_id) FROM stdin;
\.


--
-- Data for Name: route; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.route (id, bootstrap_id, created_date, deleted, modified_date, description, title, configuration, deploy, end_id, start_id) FROM stdin;
\.


--
-- Data for Name: route_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.route_additional (route_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: route_output; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.route_output (route_id, output_id) FROM stdin;
\.


--
-- Data for Name: route_steps; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.route_steps (route_id, steps_id) FROM stdin;
\.


--
-- Data for Name: subscription; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.subscription (id, bootstrap_id, created_date, deleted, modified_date, description, title, ids_protocol, location, push_data, subscriber, target) FROM stdin;
\.


--
-- Data for Name: subscription_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.subscription_additional (subscription_id, additional, additional_key) FROM stdin;
\.


--
-- Data for Name: truststore; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.truststore (id, bootstrap_id, created_date, deleted, modified_date, alias, location, password) FROM stdin;
4ddfccf4-ea8a-49e4-8b53-d963a563cf3a	\N	2021-10-08 11:52:53.379678	f	2021-10-08 11:52:53.379697	1	file:///conf/truststore.p12
\.


--
-- Data for Name: truststore_additional; Type: TABLE DATA; Schema: public; Owner: connector
--

COPY public.truststore_additional (truststore_id, additional, additional_key) FROM stdin;
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
-- Name: authentication authentication_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.authentication
    ADD CONSTRAINT authentication_pkey PRIMARY KEY (id);


--
-- Name: broker_additional broker_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.broker_additional
    ADD CONSTRAINT broker_additional_pkey PRIMARY KEY (broker_id, additional_key);


--
-- Name: broker broker_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.broker
    ADD CONSTRAINT broker_pkey PRIMARY KEY (id);


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
-- Name: configuration_additional configuration_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration_additional
    ADD CONSTRAINT configuration_additional_pkey PRIMARY KEY (configuration_id, additional_key);


--
-- Name: configuration configuration_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT configuration_pkey PRIMARY KEY (id);


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
-- Name: data_source_additional data_source_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.data_source_additional
    ADD CONSTRAINT data_source_additional_pkey PRIMARY KEY (data_source_id, additional_key);


--
-- Name: datasource datasource_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.datasource
    ADD CONSTRAINT datasource_pkey PRIMARY KEY (id);


--
-- Name: endpoint_additional endpoint_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.endpoint_additional
    ADD CONSTRAINT endpoint_additional_pkey PRIMARY KEY (endpoint_id, additional_key);


--
-- Name: endpoint endpoint_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.endpoint
    ADD CONSTRAINT endpoint_pkey PRIMARY KEY (id);


--
-- Name: keystore_additional keystore_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.keystore_additional
    ADD CONSTRAINT keystore_additional_pkey PRIMARY KEY (keystore_id, additional_key);


--
-- Name: keystore keystore_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.keystore
    ADD CONSTRAINT keystore_pkey PRIMARY KEY (id);


--
-- Name: proxy_additional proxy_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.proxy_additional
    ADD CONSTRAINT proxy_additional_pkey PRIMARY KEY (proxy_id, additional_key);


--
-- Name: proxy proxy_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.proxy
    ADD CONSTRAINT proxy_pkey PRIMARY KEY (id);


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
-- Name: route_additional route_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_additional
    ADD CONSTRAINT route_additional_pkey PRIMARY KEY (route_id, additional_key);


--
-- Name: route route_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route
    ADD CONSTRAINT route_pkey PRIMARY KEY (id);


--
-- Name: subscription_additional subscription_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.subscription_additional
    ADD CONSTRAINT subscription_additional_pkey PRIMARY KEY (subscription_id, additional_key);


--
-- Name: subscription subscription_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT subscription_pkey PRIMARY KEY (id);


--
-- Name: truststore_additional truststore_additional_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.truststore_additional
    ADD CONSTRAINT truststore_additional_pkey PRIMARY KEY (truststore_id, additional_key);


--
-- Name: truststore truststore_pkey; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.truststore
    ADD CONSTRAINT truststore_pkey PRIMARY KEY (id);


--
-- Name: route_steps uk_2hvysc5saqk4y3qexekw015; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_steps
    ADD CONSTRAINT uk_2hvysc5saqk4y3qexekw015 UNIQUE (steps_id);


--
-- Name: artifact_subscriptions uk_8g340vntvcp4knscsvy1bgopj; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact_subscriptions
    ADD CONSTRAINT uk_8g340vntvcp4knscsvy1bgopj UNIQUE (subscriptions_id);


--
-- Name: configuration uk_bj5efn7lht054mm1nfr2rscud; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT uk_bj5efn7lht054mm1nfr2rscud UNIQUE (active);


--
-- Name: data_authentication uk_dkhd0daisu2wkfca4tls8ekvl; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.data_authentication
    ADD CONSTRAINT uk_dkhd0daisu2wkfca4tls8ekvl UNIQUE (authentication_id);


--
-- Name: representation_subscriptions uk_dsf5lslfbac5d120rqsprgfqo; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation_subscriptions
    ADD CONSTRAINT uk_dsf5lslfbac5d120rqsprgfqo UNIQUE (subscriptions_id);


--
-- Name: resource_subscriptions uk_l81if7mwa7ftd3st366piovp1; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_subscriptions
    ADD CONSTRAINT uk_l81if7mwa7ftd3st366piovp1 UNIQUE (subscriptions_id);


--
-- Name: route_output uk_luhfgdmupuurnoxtybwl25jc4; Type: CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_output
    ADD CONSTRAINT uk_luhfgdmupuurnoxtybwl25jc4 UNIQUE (output_id);


--
-- Name: route fk1g3lo2scgxrotyuyiuiptekiq; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route
    ADD CONSTRAINT fk1g3lo2scgxrotyuyiuiptekiq FOREIGN KEY (end_id) REFERENCES public.endpoint(id);


--
-- Name: artifact_additional fk39nygv2yn0osg687e1a42njrv; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact_additional
    ADD CONSTRAINT fk39nygv2yn0osg687e1a42njrv FOREIGN KEY (artifact_id) REFERENCES public.artifact(id);


--
-- Name: configuration fk4h4a0pt25jjihdcuyykh5j0i1; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT fk4h4a0pt25jjihdcuyykh5j0i1 FOREIGN KEY (truststore_id) REFERENCES public.truststore(id);


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
-- Name: keystore_additional fk6f9sjbayqd2tmactm5rppom9c; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.keystore_additional
    ADD CONSTRAINT fk6f9sjbayqd2tmactm5rppom9c FOREIGN KEY (keystore_id) REFERENCES public.keystore(id);


--
-- Name: configuration_inbound_model_version fk6llo3rw3vr0e29yxx58smva9d; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration_inbound_model_version
    ADD CONSTRAINT fk6llo3rw3vr0e29yxx58smva9d FOREIGN KEY (configuration_id) REFERENCES public.configuration(id);


--
-- Name: artifact_subscriptions fk6mfm8q9bn2err5r1fhjjppepp; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact_subscriptions
    ADD CONSTRAINT fk6mfm8q9bn2err5r1fhjjppepp FOREIGN KEY (artifact_id) REFERENCES public.artifact(id);


--
-- Name: subscription_additional fk71ubo9u0s73svem5lkcc78r98; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.subscription_additional
    ADD CONSTRAINT fk71ubo9u0s73svem5lkcc78r98 FOREIGN KEY (subscription_id) REFERENCES public.subscription(id);


--
-- Name: proxy_additional fk74baqqhhlkqcosactxv0rj3x5; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.proxy_additional
    ADD CONSTRAINT fk74baqqhhlkqcosactxv0rj3x5 FOREIGN KEY (proxy_id) REFERENCES public.proxy(id);


--
-- Name: agreement_artifacts fk7mjl82ejj66tmmrkspyoyailp; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.agreement_artifacts
    ADD CONSTRAINT fk7mjl82ejj66tmmrkspyoyailp FOREIGN KEY (agreements_id) REFERENCES public.agreement(id);


--
-- Name: data_authentication fk81c700cmspub1da1nyqsispc7; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.data_authentication
    ADD CONSTRAINT fk81c700cmspub1da1nyqsispc7 FOREIGN KEY (authentication_id) REFERENCES public.authentication(id);


--
-- Name: route_steps fk94sxynhfrl9q58pp4b7mj0mf9; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_steps
    ADD CONSTRAINT fk94sxynhfrl9q58pp4b7mj0mf9 FOREIGN KEY (steps_id) REFERENCES public.route(id);


--
-- Name: catalog_offered_resources fk98lr0lvbouf53nx6e2m51p36f; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_offered_resources
    ADD CONSTRAINT fk98lr0lvbouf53nx6e2m51p36f FOREIGN KEY (catalogs_id) REFERENCES public.catalog(id);


--
-- Name: datasource fka61ig1wcffdnldgy6ktghysnt; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.datasource
    ADD CONSTRAINT fka61ig1wcffdnldgy6ktghysnt FOREIGN KEY (authentication_id) REFERENCES public.authentication(id);


--
-- Name: contract_rules fka7kvb987lhl2yi5m2ayrgr6kk; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.contract_rules
    ADD CONSTRAINT fka7kvb987lhl2yi5m2ayrgr6kk FOREIGN KEY (rules_id) REFERENCES public.contractrule(id);


--
-- Name: route_output fkbcdklj53uxgkwbu5gnpa4o4r6; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_output
    ADD CONSTRAINT fkbcdklj53uxgkwbu5gnpa4o4r6 FOREIGN KEY (output_id) REFERENCES public.artifact(id);


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
-- Name: configuration fkcnuk54rswo29k0oknie4ig87p; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT fkcnuk54rswo29k0oknie4ig87p FOREIGN KEY (proxy_id) REFERENCES public.proxy(id);


--
-- Name: representation_subscriptions fkcrvkeo1my0i7btc1hl0vjrp0k; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation_subscriptions
    ADD CONSTRAINT fkcrvkeo1my0i7btc1hl0vjrp0k FOREIGN KEY (representation_id) REFERENCES public.representation(id);


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
-- Name: broker_offered_resources fkdq2d30t4k6sgeprpo8er53au2; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.broker_offered_resources
    ADD CONSTRAINT fkdq2d30t4k6sgeprpo8er53au2 FOREIGN KEY (offered_resources_id) REFERENCES public.resource(id);


--
-- Name: representation_subscriptions fkejubnajlqcowcxfuy6ddbhl0o; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.representation_subscriptions
    ADD CONSTRAINT fkejubnajlqcowcxfuy6ddbhl0o FOREIGN KEY (subscriptions_id) REFERENCES public.subscription(id);


--
-- Name: catalog_additional fkf1tu8lci4kjy9b1mw5wnxht0g; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.catalog_additional
    ADD CONSTRAINT fkf1tu8lci4kjy9b1mw5wnxht0g FOREIGN KEY (catalog_id) REFERENCES public.catalog(id);


--
-- Name: route_steps fkf9bb67m7ur0e99ok4dshqk5og; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_steps
    ADD CONSTRAINT fkf9bb67m7ur0e99ok4dshqk5og FOREIGN KEY (route_id) REFERENCES public.route(id);


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
-- Name: configuration_additional fkh81j3cxbwx7bsx5ood605vcjt; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration_additional
    ADD CONSTRAINT fkh81j3cxbwx7bsx5ood605vcjt FOREIGN KEY (configuration_id) REFERENCES public.configuration(id);


--
-- Name: endpoint fkhaa3q1mhmewea5d0cek83lf8q; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.endpoint
    ADD CONSTRAINT fkhaa3q1mhmewea5d0cek83lf8q FOREIGN KEY (data_source_id) REFERENCES public.datasource(id);


--
-- Name: endpoint_additional fkhaseuf282r6ygev7hk5tl30to; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.endpoint_additional
    ADD CONSTRAINT fkhaseuf282r6ygev7hk5tl30to FOREIGN KEY (endpoint_id) REFERENCES public.endpoint(id);


--
-- Name: resource_additional fkii3qb000uovadjpmgnyk4i2oy; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_additional
    ADD CONSTRAINT fkii3qb000uovadjpmgnyk4i2oy FOREIGN KEY (resource_id) REFERENCES public.resource(id);


--
-- Name: broker_offered_resources fkjnwllwpuby698lrle9yo0x6s; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.broker_offered_resources
    ADD CONSTRAINT fkjnwllwpuby698lrle9yo0x6s FOREIGN KEY (brokers_id) REFERENCES public.broker(id);


--
-- Name: proxy fkjqodc488drfb2mrr31dni7eu4; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.proxy
    ADD CONSTRAINT fkjqodc488drfb2mrr31dni7eu4 FOREIGN KEY (authentication_id) REFERENCES public.authentication(id);


--
-- Name: agreement_artifacts fkk81vbye4esds4rdyhna320vt7; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.agreement_artifacts
    ADD CONSTRAINT fkk81vbye4esds4rdyhna320vt7 FOREIGN KEY (artifacts_id) REFERENCES public.artifact(id);


--
-- Name: proxy_exclusions fkkhj2sdlpcm77dv89nlbaainy6; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.proxy_exclusions
    ADD CONSTRAINT fkkhj2sdlpcm77dv89nlbaainy6 FOREIGN KEY (proxy_id) REFERENCES public.proxy(id);


--
-- Name: data_authentication fkkl77xdnuxec8upj9lugdv7v5h; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.data_authentication
    ADD CONSTRAINT fkkl77xdnuxec8upj9lugdv7v5h FOREIGN KEY (remote_data_id) REFERENCES public.data(id);


--
-- Name: resource_subscriptions fkkqdlegy1kfu7so51otnnt8d93; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_subscriptions
    ADD CONSTRAINT fkkqdlegy1kfu7so51otnnt8d93 FOREIGN KEY (resource_id) REFERENCES public.resource(id);


--
-- Name: route_output fklf1uposbe7ucotdqouphlbpua; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_output
    ADD CONSTRAINT fklf1uposbe7ucotdqouphlbpua FOREIGN KEY (route_id) REFERENCES public.route(id);


--
-- Name: route_additional fklhkid1suvl4oyk4ccqa88pkcc; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route_additional
    ADD CONSTRAINT fklhkid1suvl4oyk4ccqa88pkcc FOREIGN KEY (route_id) REFERENCES public.route(id);


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
-- Name: route fkn2bo3q5fqan770va5dhwel55d; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.route
    ADD CONSTRAINT fkn2bo3q5fqan770va5dhwel55d FOREIGN KEY (start_id) REFERENCES public.endpoint(id);


--
-- Name: data_source_additional fko7xkkayhab49fqu2l43mky4vk; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.data_source_additional
    ADD CONSTRAINT fko7xkkayhab49fqu2l43mky4vk FOREIGN KEY (data_source_id) REFERENCES public.datasource(id);


--
-- Name: resource_subscriptions fkpbsa263kgj7oqeq5q4o0buig8; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_subscriptions
    ADD CONSTRAINT fkpbsa263kgj7oqeq5q4o0buig8 FOREIGN KEY (subscriptions_id) REFERENCES public.subscription(id);


--
-- Name: configuration fkq60ebey7biokeci38he1b4qpn; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.configuration
    ADD CONSTRAINT fkq60ebey7biokeci38he1b4qpn FOREIGN KEY (keystore_id) REFERENCES public.keystore(id);


--
-- Name: broker_additional fkqlaqe74yts5ur3vmjvv9o2n2f; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.broker_additional
    ADD CONSTRAINT fkqlaqe74yts5ur3vmjvv9o2n2f FOREIGN KEY (broker_id) REFERENCES public.broker(id);


--
-- Name: truststore_additional fkqvuo1817a0oh4atlowx0mn61h; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.truststore_additional
    ADD CONSTRAINT fkqvuo1817a0oh4atlowx0mn61h FOREIGN KEY (truststore_id) REFERENCES public.truststore(id);


--
-- Name: resource_samples fkrqbwef08s1vqba7f5ank6tmva; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_samples
    ADD CONSTRAINT fkrqbwef08s1vqba7f5ank6tmva FOREIGN KEY (resource_id) REFERENCES public.resource(id);


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
-- Name: artifact_subscriptions fkt0bwn5a7yqtpxq7g2485yo8wu; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.artifact_subscriptions
    ADD CONSTRAINT fkt0bwn5a7yqtpxq7g2485yo8wu FOREIGN KEY (subscriptions_id) REFERENCES public.subscription(id);


--
-- Name: resource_representations fktntca482f3j16nwbrb9y6y51l; Type: FK CONSTRAINT; Schema: public; Owner: connector
--

ALTER TABLE ONLY public.resource_representations
    ADD CONSTRAINT fktntca482f3j16nwbrb9y6y51l FOREIGN KEY (resources_id) REFERENCES public.resource(id);


--
-- PostgreSQL database dump complete
--
