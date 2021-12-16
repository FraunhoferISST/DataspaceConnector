CREATE TABLE public.authentication (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    deleted boolean DEFAULT false,
    password character varying(255),
    username character varying(255),
    key character varying(255),
    value character varying(255)
);

CREATE TABLE public.data_authentication (
    remote_data_id bigint NOT NULL,
    authentication_id bigint NOT NULL
);

ALTER TABLE ONLY public.authentication
    ADD CONSTRAINT authentication_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.data_authentication
    ADD CONSTRAINT uk_dkhd0daisu2wkfca4tls8ekvl UNIQUE (authentication_id);

ALTER TABLE ONLY public.data_authentication
    ADD CONSTRAINT fk81c700cmspub1da1nyqsispc7 FOREIGN KEY (authentication_id) REFERENCES public.authentication(id);

ALTER TABLE ONLY public.data_authentication
    ADD CONSTRAINT fkkl77xdnuxec8upj9lugdv7v5h FOREIGN KEY (remote_data_id) REFERENCES public.data(id);

SELECT lowrite(lo_open(value::oid, x'60000'::int), decode(regexp_replace(encode(lo_get(value::oid), 'escape'),
    'idsc:', 'https://w3id.org/idsa/code/'), 'escape')) FROM public.agreement;

SELECT lowrite(lo_open(value::oid, x'60000'::int), decode(regexp_replace(encode(lo_get(value::oid), 'escape'),
    'idsc:', 'https://w3id.org/idsa/code/'), 'escape')) FROM public.contractrule;
