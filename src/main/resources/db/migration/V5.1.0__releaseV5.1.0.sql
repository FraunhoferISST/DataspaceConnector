ALTER TABLE public.agreement
    ALTER COLUMN remote_id TYPE character varying(2048);

ALTER TABLE public.artifact
    ALTER COLUMN remote_address TYPE character varying(2048);

ALTER TABLE public.artifact
    ALTER COLUMN remote_id TYPE character varying(2048);

ALTER TABLE public.contract
    ALTER COLUMN consumer TYPE character varying(2048);

ALTER TABLE public.contract
    ALTER COLUMN provider TYPE character varying(2048);

ALTER TABLE public.contract
    ALTER COLUMN remote_id TYPE character varying(2048);

ALTER TABLE public.contractrule
    ALTER COLUMN remote_id TYPE character varying(2048);

ALTER TABLE public.data
    ALTER COLUMN access_url TYPE character varying(2048);

ALTER TABLE public.representation
    ALTER COLUMN remote_id TYPE character varying(2048);

ALTER TABLE public.resource
    ALTER COLUMN endpoint_documentation TYPE character varying(2048);

ALTER TABLE public.resource
    ALTER COLUMN licence TYPE character varying(2048);

ALTER TABLE public.resource
    ALTER COLUMN publisher TYPE character varying(2048);

ALTER TABLE public.resource
    ALTER COLUMN sovereign TYPE character varying(2048);

ALTER TABLE public.resource
    ALTER COLUMN remote_id TYPE character varying(2048);
