ALTER TABLE public.agreement
    ADD COLUMN bootstrap_id bytea;

ALTER TABLE public.artifact
    ADD COLUMN bootstrap_id bytea;

ALTER TABLE public.catalog
    ADD COLUMN bootstrap_id bytea;

ALTER TABLE public.contract
    ADD COLUMN bootstrap_id bytea;

ALTER TABLE public.contractrule
    ADD COLUMN bootstrap_id bytea;

ALTER TABLE public.representation
    ADD COLUMN bootstrap_id bytea;

ALTER TABLE public.resource
    ADD COLUMN bootstrap_id bytea;
