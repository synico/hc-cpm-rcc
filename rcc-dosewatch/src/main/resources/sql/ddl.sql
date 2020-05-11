ALTER TABLE device
    ADD COLUMN purchase_date varchar(255) default to_char(now(),'YYYY-MM-DD'),
    ADD COLUMN purchase_price numeric(10,2) default 0.0,
    ADD COLUMN salvage_value numeric(10,2) default 0.0,
    ADD COLUMN lifecycle int4 default 1,
    ADD COLUMN depreciation_method int4 default 1;

alter table device add column province varchar(255);
alter table device add column city varchar(255);

CREATE TABLE body_part_mapping (
    org_id integer NOT NULL,
    aet varchar(255) NOT NULL,
    device_type varchar(255) NOT NULL,
    protocol_key bigint NOT NULL,
    protocol_name varchar(255) NOT NULL,
    body_category varchar(255) NOT NULL,
    device_key varchar(255) NOT NULL
)