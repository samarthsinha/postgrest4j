CREATE TABLE IF NOT EXISTS master_configurations
(
 _id  serial,
 _tenant_id varchar(255),
 _org_id varchar(255),
 master_name varchar(255),
 _master_table_name varchar(255),
 columns_details jsonb,
 _published boolean,
 _active boolean,
 _created_at TIMESTAMP ,
 _updated_at TIMESTAMP ,
 _updated_by varchar (25),
 PRIMARY KEY (_id)
);