ALTER TABLE public.app
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.appstore
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.artifact
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.artifact
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.broker
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.catalog
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.configuration
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.contract
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.contractrule
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.representation
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.resource
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.route
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.subscription
    ALTER COLUMN description TYPE character varying(2048);

ALTER TABLE public.authentication
    ALTER COLUMN "key" TYPE character varying(2048),
    ALTER COLUMN "value" TYPE character varying(2048),
    ALTER COLUMN password TYPE character varying(2048),
    ALTER COLUMN username TYPE character varying(2048);
