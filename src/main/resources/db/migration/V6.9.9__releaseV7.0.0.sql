TRUNCATE TABLE public.configuration CASCADE;

ALTER TABLE public.app
    ADD COLUMN container_name character varying(255);

UPDATE public.endpoint AS e
SET data_source_id = NULL
    FROM public.datasource AS d
WHERE d.type = 'DATABASE'
  AND d.id = e.data_source_id;

DELETE FROM public.datasource
WHERE "type" = 'DATABASE';

ALTER TABLE public.datasource
DROP COLUMN "type",
    ADD COLUMN dtype character varying(31) NOT NULL DEFAULT 'DataSource',
    ADD COLUMN driver_class_name character varying(255),
    ADD COLUMN url character varying(255);

ALTER TABLE public.endpoint
    ADD COLUMN path character varying(255),
    ADD COLUMN exposed_port integer;

ALTER TABLE public.route
    ADD COLUMN output_id uuid;

ALTER TABLE ONLY public.route
    ADD CONSTRAINT fkp1ks1pqybwte5899wtu1vav55 FOREIGN KEY (output_id) REFERENCES public.artifact(id);

DROP TABLE public.route_output;

UPDATE public.route AS r
SET start_id = NULL
    FROM public.endpoint AS e
WHERE e.dtype = 'ConnectorEndpoint'
  AND e.id = r.start_id;

UPDATE public.route AS r
SET end_id = NULL
    FROM public.endpoint AS e
WHERE e.dtype = 'ConnectorEndpoint'
  AND e.id = r.end_id;

DELETE FROM public.endpoint
WHERE dtype = 'ConnectorEndpoint';

ALTER TABLE public.appstore DROP CONSTRAINT uk_jq36lxpgxfs4ktn7wrq28wo0f;
